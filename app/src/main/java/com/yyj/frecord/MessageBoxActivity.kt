package com.yyj.frecord

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message_box.*
import kotlinx.android.synthetic.main.activity_message_box.layoutEdit
import kotlinx.android.synthetic.main.layout_edit.*

class MessageBoxActivity : AppCompatActivity() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    private lateinit var msgBoxAdapter: MessageBoxAdapter
    private val msgList = arrayListOf<MessageData>()
    lateinit var itemCheckListener: MessageBoxAdapter.OnItemClickListener
    lateinit var itemLongClickListener: MessageBoxAdapter.OnItemLongClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_box)
        dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase
        initList()
        setButton()
    }

    private fun initList() {
        itemCheckListener = object : MessageBoxAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val cbDelete = view.findViewById<CheckBox>(R.id.cbEditMsgBox)
                cbDelete.isChecked = !cbDelete.isChecked
            }
        }
        itemLongClickListener = object : MessageBoxAdapter.OnItemLongClickListener {
            override fun onLongClick(view: View, position: Int): Boolean {
                if (layoutEdit.visibility == View.GONE) {
                    setAdapter(true)
                }
                view.findViewById<CheckBox>(R.id.cbEditMsgBox).isChecked = true
                return true
            }
        }
        rvMsgBox.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        setAdapter(false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        setListData()
        msgBoxAdapter.notifyDataSetChanged()
    }

    @SuppressLint("Range")
    fun setListData() {
        msgList.clear()
        val c : Cursor = db.rawQuery("SELECT * FROM ${DBHelper.MSG_TABLE}", null)
        while (c.moveToNext()) {
            val date = c.getInt(c.getColumnIndex(DBHelper.MSG_COL_DATE)) * 1000L
            if (date <= System.currentTimeMillis()) {
                val id = c.getInt(c.getColumnIndex(DBHelper.MSG_COL_ID))
                val content = c.getString(c.getColumnIndex(DBHelper.MSG_COL_CONTENT))
                msgList.add(MessageData(id, date, content, null, false))
                val req = c.getInt(c.getColumnIndex(DBHelper.MSG_COL_REQ))
                delNotificationChannel(req.toString())
            }
        }
        c.close()
        msgList.sortBy { messageData -> messageData.date }
        rvMsgBox.scrollToPosition(msgList.size - 1)
    }

    private fun delNotificationChannel(id: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(id) != null) {
                notificationManager.deleteNotificationChannel(id)
            }
        }
    }

    private fun setAdapter(edit : Boolean) {
        msgBoxAdapter = MessageBoxAdapter(this, msgList, edit)
        if (edit) {
            layoutEdit.visibility = View.VISIBLE
        }
        else {
            layoutEdit.visibility = View.GONE
        }
        msgBoxAdapter.setItemClickListener(itemCheckListener)
        msgBoxAdapter.setItemLongClickListener(itemLongClickListener)
        rvMsgBox.adapter = msgBoxAdapter
    }

    private fun setButton() {
        btnEditDel.setOnClickListener {
            var checked = false
            val msgListIterator = msgList.iterator()
            while (msgListIterator.hasNext()) {
                val msg = msgListIterator.next()
                if (msg.checked) {
                    checked = true
                    msgListIterator.remove()
                    db.execSQL("DELETE FROM ${DBHelper.MSG_TABLE} WHERE _id = ${msg.id}")
                }
            }
            if (checked) {
                setAdapter(false)
            }
        }
        btnEditCancel.setOnClickListener {
            val msgListIterator = msgList.iterator()
            while (msgListIterator.hasNext()) {
                val msg = msgListIterator.next()
                if (msg.checked) {
                    msg.checked = false
                }
            }
            setAdapter(false)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.close()
    }
}
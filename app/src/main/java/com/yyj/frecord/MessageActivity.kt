package com.yyj.frecord

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_message.layoutEdit
import kotlinx.android.synthetic.main.layout_bottom.*
import kotlinx.android.synthetic.main.layout_edit.*

class MessageActivity : Fragment() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    lateinit var msgListAdapter: MessageListAdapter
    private lateinit var ctx : Context
    private lateinit var itemCheckListener: MessageListAdapter.OnItemClickListener
    private lateinit var itemLongClickListener: MessageListAdapter.OnItemLongClickListener
    val msgList = arrayListOf<MessageData>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dbHelper = DBHelper(ctx)
        db = dbHelper.writableDatabase
        icMenu1.setImageResource(R.drawable.ic_selected_view)
        icMenu2.setImageResource(R.drawable.ic_not_selected_view)
        val sharedPref = ctx.getSharedPreferences("setting", Context.MODE_PRIVATE)
        tvToUserName.text = sharedPref.getString("name", null)
        initList()
        if (msgList.size > 0) {
            tvMsgExplain.visibility = View.INVISIBLE
        }
        setButton()
    }

    private fun initList() {
        setListData()
        itemCheckListener = object : MessageListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val cbDelete = view.findViewById<CheckBox>(R.id.cbEditMsg)
                cbDelete.isChecked = !cbDelete.isChecked
            }
        }
        itemLongClickListener = object : MessageListAdapter.OnItemLongClickListener {
            override fun onLongClick(view: View, position: Int): Boolean {
                if (layoutEdit.visibility == View.GONE) {
                    msgList[position].checked = true
                    setAdapter(true)
                }
                view.findViewById<CheckBox>(R.id.cbEditMsg).isChecked = true
                return true
            }
        }
        rvMsg.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        setAdapter(false)
    }

    @SuppressLint("Range")
    fun setListData() {
        msgList.clear()
        val c : Cursor = db.rawQuery("SELECT _id, date, req FROM ${DBHelper.MSG_TABLE}", null)
        while (c.moveToNext()) {
            val date = c.getInt(c.getColumnIndex(DBHelper.MSG_COL_DATE)) * 1000L
            if (date > System.currentTimeMillis()) {
                val id = c.getInt(c.getColumnIndex(DBHelper.MSG_COL_ID))
                val req = c.getInt(c.getColumnIndex(DBHelper.MSG_COL_REQ))
                msgList.add(MessageData(id, date, null, req, false))
            }
        }
        c.close()
        msgList.sortByDescending { messageData -> messageData.date }
    }

    private fun setAdapter(edit : Boolean) {
        msgListAdapter = MessageListAdapter(ctx, msgList, edit)
        if (edit) {
            layoutEdit.visibility = View.VISIBLE
            btnAddMsg.visibility = View.GONE
        }
        else {
            layoutEdit.visibility = View.GONE
            btnAddMsg.visibility = View.VISIBLE
        }
        msgListAdapter.setItemClickListener(itemCheckListener)
        msgListAdapter.setItemLongClickListener(itemLongClickListener)
        rvMsg.adapter = msgListAdapter
    }

    private fun setButton() {
        btnAddMsg.setOnClickListener {
            val intent = Intent(ctx, AddMessageActivity::class.java)
            startActivity(intent)
        }
        btnMsgBox.setOnClickListener {
            val intent = Intent(ctx, MessageBoxActivity::class.java)
            startActivity(intent)
        }
        btnEditDel.setOnClickListener {
            var checked = false
            val msgListIterator = msgList.iterator()
            while (msgListIterator.hasNext()) {
                val msg = msgListIterator.next()
                if (msg.checked) {
                    checked = true
                    msgListIterator.remove()
                    if (msg.date > System.currentTimeMillis()) {
                        db.execSQL("DELETE FROM ${DBHelper.MSG_TABLE} WHERE _id = ${msg.id}")
                        val intent = Intent(ctx, AlarmReceiver::class.java)
                        val alarmManager =
                            ctx.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                        val pendingIntent =
                            PendingIntent.getService(context, msg.req!!, intent,
                                PendingIntent.FLAG_NO_CREATE)
                        if (pendingIntent != null && alarmManager != null) {
                            alarmManager.cancel(pendingIntent)
                        }
                    }
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
}
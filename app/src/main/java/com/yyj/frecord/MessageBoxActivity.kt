package com.yyj.frecord

import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message_box.*
import kotlinx.android.synthetic.main.activity_message_box.layoutEdit
import kotlinx.android.synthetic.main.layout_edit.*

class MessageBoxActivity : AppCompatActivity() {
    private lateinit var msgBoxAdapter: MessageBoxAdapter
    private val msgList = arrayListOf<MessageData>()
    lateinit var itemCheckListener: MessageBoxAdapter.OnItemClickListener
    lateinit var itemLongClickListener: MessageBoxAdapter.OnItemLongClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_box)
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

        msgList.add(MessageData(0, System.currentTimeMillis(), "메시지1111111111111111111", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "메시지2\n메시지", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "메시지3", false))

        setAdapter(false)
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
                    //db 삭제
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
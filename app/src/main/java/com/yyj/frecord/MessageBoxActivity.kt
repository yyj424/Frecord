package com.yyj.frecord

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message_box.*

class MessageBoxActivity : AppCompatActivity() {
    private lateinit var msgBoxAdapter: MessageBoxAdapter
    private val msgList = arrayListOf<MessageData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_box)
        initList()
        icDelMsgBox.setOnClickListener {
            msgBoxAdapter = MessageBoxAdapter(this, msgList, true)
            rvMsgBox.adapter = msgBoxAdapter
        }
    }

    private fun initList() {
        rvMsgBox.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        msgBoxAdapter = MessageBoxAdapter(this, msgList, false)
        rvMsgBox.adapter = msgBoxAdapter
        msgList.add(MessageData(0, System.currentTimeMillis(), "메시지1111111111111111111", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "메시지2\n메시지", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "메시지3", false))

        msgBoxAdapter.notifyDataSetChanged()
    }
}
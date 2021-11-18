package com.yyj.frecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.layout_bottom.*

class MessageActivity : Fragment() {
    private lateinit var msgListAdapter: MessageListAdapter
    private lateinit var ctx : Context
    private val msgList = arrayListOf<MessageData>()

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
        icMenu1.setImageResource(R.drawable.ic_selected_view)
        icMenu2.setImageResource(R.drawable.ic_not_selected_view)
        val sharedPref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE)
        tvToUserName.text = sharedPref.getString("name", null)
        initList()
        if (msgList.size > 0) {
            tvMsgExplain.visibility = View.INVISIBLE
        }
        btnAddMsg.setOnClickListener {
            val intent = Intent(ctx, AddMessageActivity::class.java)
            startActivity(intent)
        }
        icMsgBox.setOnClickListener {
            val intent = Intent(ctx, MessageBoxActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initList() {
        rvMsg.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        msgListAdapter = MessageListAdapter(ctx, msgList, false)
        rvMsg.adapter = msgListAdapter
        msgList.add(MessageData(0, System.currentTimeMillis(), "", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "", false))

        msgListAdapter.notifyDataSetChanged()
    }
}
package com.yyj.frecord

import android.content.Context
import android.content.Intent
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
    private lateinit var msgListAdapter: MessageListAdapter
    private lateinit var ctx : Context
    private lateinit var itemCheckListener: MessageListAdapter.OnItemClickListener
    lateinit var itemLongClickListener: MessageListAdapter.OnItemLongClickListener
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
        setButton()
    }

    private fun initList() {
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

        msgList.add(MessageData(0, System.currentTimeMillis(), "", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "", false))
        msgList.add(MessageData(0, System.currentTimeMillis(), "", false))

        setAdapter(false)
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
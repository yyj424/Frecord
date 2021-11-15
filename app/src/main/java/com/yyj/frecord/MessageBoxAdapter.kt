package com.yyj.frecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MessageBoxAdapter (private val context : Context, private val list : ArrayList<MessageData>, private val edit : Boolean) : RecyclerView.Adapter<MessageBoxAdapter.ViewHolder>() {
    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_message_box, parent, false)

        return ViewHolder(view, edit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msgData = list[position]
        holder.getMessage(msgData)
    }

    inner class ViewHolder(view: View, private val edit : Boolean) : RecyclerView.ViewHolder(view) {
        private val tvDate : TextView = view.findViewById(R.id.tvMsgBoxDate)
        private val tvContent : TextView = view.findViewById(R.id.tvMsgBoxContent)
        private val cbEdit : CheckBox = view.findViewById(R.id.cbEditMsgBox)
        private val dateFormat = SimpleDateFormat("yyyy. MM. dd. HH : mm")

        fun getMessage(msgData: MessageData){
            if (edit) {
                cbEdit.visibility = View.VISIBLE
            }
            else {
                cbEdit.visibility = View.GONE
            }
            cbEdit.isChecked = msgData.checked
            tvContent.text = msgData.content
            tvDate.text = dateFormat.format(msgData.date)
            cbEdit.setOnCheckedChangeListener { _, isChecked ->
                msgData.checked = isChecked
            }
        }
    }
}
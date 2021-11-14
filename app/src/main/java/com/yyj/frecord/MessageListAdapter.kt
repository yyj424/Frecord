package com.yyj.frecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class MessageListAdapter (private val context : Context, private val list : ArrayList<MessageData>, private val edit : Boolean) : RecyclerView.Adapter<MessageListAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_message_list, parent, false)

        return ViewHolder(view, edit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val msgData = list[position]
        holder.getMessage(msgData)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    inner class ViewHolder(view: View, private val edit : Boolean) : RecyclerView.ViewHolder(view) {

        private val date = SimpleDateFormat("yyyy. MM. dd")
        fun getMessage(msgData: MessageData){

        }
    }
}
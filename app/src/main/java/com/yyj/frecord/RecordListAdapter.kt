package com.yyj.frecord

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class RecordListAdapter (private val context : Context, private val list : ArrayList<RecordData>, private val edit : Boolean) : RecyclerView.Adapter<RecordListAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }
    interface OnItemLongClickListener {
        fun onLongClick(view: View, position: Int) : Boolean
    }

    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var itemLongClickListener : OnItemLongClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }
    fun setItemLongClickListener(itemLongClickListener: OnItemLongClickListener) {
        this.itemLongClickListener = itemLongClickListener
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_view_record_list, parent, false)
        return ViewHolder(view, edit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordData = list[position]
        holder.getRecord(recordData)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener.onLongClick(it, position)
        }
    }

    inner class ViewHolder(view: View, private val edit : Boolean) : RecyclerView.ViewHolder(view) {
        private val tvTitle : TextView = view.findViewById(R.id.tvRecordTitle)
        private val tvDate : TextView = view.findViewById(R.id.tvRecordDate)
        private val cbEdit : CheckBox = view.findViewById(R.id.cbEditRecord)
        private val ivLock : ImageView = view.findViewById(R.id.ivLock)
        private val dateFormat = SimpleDateFormat("yyyy. MM. dd")

        fun getRecord(recordData: RecordData){
            if (edit) {
                cbEdit.visibility = View.VISIBLE
                cbEdit.isChecked = recordData.checked
            }
            else {
                cbEdit.visibility = View.GONE
            }
            if (!TextUtils.isEmpty(recordData.title?.trim())) {
                tvTitle.text = recordData.title
                tvDate.text = dateFormat.format(recordData.date)
            }
            else {
                tvTitle.text = dateFormat.format(recordData.date)
                tvDate.visibility = View.INVISIBLE
            }
            if (recordData.locked == 1) {
                ivLock.visibility = View.VISIBLE
            }
            cbEdit.setOnCheckedChangeListener { _, isChecked ->
                recordData.checked = isChecked
            }
        }
    }
}
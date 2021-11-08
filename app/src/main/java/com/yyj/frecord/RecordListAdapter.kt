package com.yyj.frecord

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class RecordListAdapter (private val context : Context, private val list : ArrayList<RecordData>, private val edit : Boolean) : RecyclerView.Adapter<RecordListAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_view_record_list, parent, false)

        return ViewHolder(view, edit)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val recordData = list[position]
        holder.getRecord(recordData)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    inner class ViewHolder(view: View, private val edit : Boolean) : RecyclerView.ViewHolder(view) {
        private val tvTitle : TextView = view.findViewById(R.id.tvRecordTitle)
        private val tvDate : TextView = view.findViewById(R.id.tvRecordDate)
        private val cbEdit : CheckBox = view.findViewById(R.id.cbEditRecord)

        private val date = SimpleDateFormat("yyyy. MM. dd")
        fun getRecord(recordData: RecordData){
            if (edit) {
                cbEdit.visibility = View.VISIBLE
            }
            else {
                cbEdit.visibility = View.GONE
            }
            cbEdit.isChecked = recordData.checked
            tvTitle.text = recordData.title
            tvDate.text = date.format(recordData.date)
            cbEdit.setOnCheckedChangeListener { _, isChecked ->
                recordData.checked = isChecked
            }
        }
    }
}
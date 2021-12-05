package com.yyj.frecord

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FCalendarAdapter (private val context : Context, private val list : ArrayList<CalendarData>) : RecyclerView.Adapter<FCalendarAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(view: View, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        this.itemClickListener = itemClickListener
    }

    override fun getItemCount() = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_view_fcalendar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val calData = list[position]
        holder.getDate(calData)
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val tvFCalDate : TextView = view.findViewById(R.id.tvFCalDate)
        private val ivFCalScore : ImageView = view.findViewById(R.id.ivFCalScore)
        fun getDate(calData: CalendarData){
            ivFCalScore.visibility = View.INVISIBLE
            if (calData.date != null) {
                tvFCalDate.text = calData.date.toString()
                if (calData.score != null) {
                    ivFCalScore.visibility = View.VISIBLE
                    when (calData.score) {
                        0 -> ivFCalScore.setImageResource(R.drawable.ic_thumb0)
                        1 -> ivFCalScore.setImageResource(R.drawable.ic_thumb1)
                        2 -> ivFCalScore.setImageResource(R.drawable.ic_thumb2)
                        3 -> ivFCalScore.setImageResource(R.drawable.ic_thumb3)
                        4 -> ivFCalScore.setImageResource(R.drawable.ic_thumb4)
                    }
                }
            }
            else {
                tvFCalDate.text = calData.date
            }
        }
    }
}
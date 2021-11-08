package com.yyj.frecord

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var recordListAdapter: RecordListAdapter
    val rdList = arrayListOf<RecordData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initList()
        if (rdList.size > 0) {
            tvExplain.visibility = View.INVISIBLE
        }
    }

    private fun initList() {
        val itemCheckListener = object : RecordListAdapter.OnItemClickListener{
            override fun onClick(view: View, position: Int) {
                Log.d("yyjLog", "pos: " + position + " title: " + rdList[position].title)
            }
        }
        rvRecord.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recordListAdapter = RecordListAdapter(this, rdList, false)
        recordListAdapter.setItemClickListener(itemCheckListener)
        rvRecord.adapter = recordListAdapter


        recordListAdapter.notifyDataSetChanged()
    }
}
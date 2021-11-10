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
import kotlinx.android.synthetic.main.activity_record.*

class RecordActivity : Fragment() {
    lateinit var recordListAdapter: RecordListAdapter
    lateinit var ctx : Context
    val rdList = arrayListOf<RecordData>()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initList()
        if (rdList.size > 0) {
            tvExplain.visibility = View.INVISIBLE
        }
        btnAddRecord.setOnClickListener {
            val intent = Intent(ctx, AddRecordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initList() {
        val itemCheckListener = object : RecordListAdapter.OnItemClickListener{
            override fun onClick(view: View, position: Int) {
                Log.d("yyjLog", "pos: " + position + " title: " + rdList[position].title)
            }
        }
        rvRecord.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        recordListAdapter = RecordListAdapter(ctx, rdList, false)
        recordListAdapter.setItemClickListener(itemCheckListener)
        rvRecord.adapter = recordListAdapter

        rdList.add(RecordData(1,"오늘의 감정기록1", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록2", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록3", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록4", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록5", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록6", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록7", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록8", System.currentTimeMillis(), 0, false))
        rdList.add(RecordData(1,"오늘의 감정기록9", System.currentTimeMillis(), 0, false))

        recordListAdapter.notifyDataSetChanged()
    }
}
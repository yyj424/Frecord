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
        val sharedPref = ctx.getSharedPreferences("user", Context.MODE_PRIVATE)
        tvUserName.text = sharedPref.getString("name", null)
        if (rdList.size > 0) {
            llExplain.visibility = View.INVISIBLE
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

        recordListAdapter.notifyDataSetChanged()
    }
}
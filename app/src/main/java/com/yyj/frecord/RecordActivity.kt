package com.yyj.frecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.layout_edit.*

class RecordActivity : Fragment() {
    lateinit var recordListAdapter: RecordListAdapter
    lateinit var ctx : Context
    lateinit var itemCheckListener: RecordListAdapter.OnItemClickListener
    private lateinit var itemClickListener: RecordListAdapter.OnItemClickListener
    lateinit var itemLongClickListener: RecordListAdapter.OnItemLongClickListener
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
        val sharedPref = ctx.getSharedPreferences("setting", Context.MODE_PRIVATE)
        tvUserName.text = sharedPref.getString("name", null)
        initList()
        if (rdList.size > 0) {
            llExplain.visibility = View.INVISIBLE
        }
        btnEditLock.visibility = View.VISIBLE
        setButton()
    }

    private fun initList() {
        itemCheckListener = object : RecordListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val cbEdit = view.findViewById<CheckBox>(R.id.cbEditRecord)
                cbEdit.isChecked = !cbEdit.isChecked
            }
        }
        itemClickListener = object : RecordListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                Log.d("yyjLog", "pos: " + position + " title: " + rdList[position].title)
            }
        }
        itemLongClickListener = object : RecordListAdapter.OnItemLongClickListener {
            override fun onLongClick(view: View, position: Int): Boolean {
                if (layoutEdit.visibility == View.GONE) {
                    rdList[position].checked = true
                    setAdapter(true)
                }
                view.findViewById<CheckBox>(R.id.cbEditRecord).isChecked = true
                return true
            }
        }
        rvRecord.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)

//        rdList.add(RecordData(0, "오늘의 기분 및 제목1", System.currentTimeMillis(), true, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목2", System.currentTimeMillis(), false, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목3", System.currentTimeMillis(), false, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목4", System.currentTimeMillis(), false, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목5", System.currentTimeMillis(), true, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목6", System.currentTimeMillis(), false, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목7", System.currentTimeMillis(), true, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목8", System.currentTimeMillis(), true, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목9", System.currentTimeMillis(), false, false))
//        rdList.add(RecordData(0, "오늘의 기분 및 제목10", System.currentTimeMillis(), false, false))

        setAdapter(false)
    }

    private fun setAdapter(edit : Boolean) {
        recordListAdapter = RecordListAdapter(ctx, rdList, edit)
        if (edit) {
            layoutEdit.visibility = View.VISIBLE
            btnAddRecord.visibility = View.GONE
            recordListAdapter.setItemClickListener(itemCheckListener)
        }
        else {
            layoutEdit.visibility = View.GONE
            btnAddRecord.visibility = View.VISIBLE
            recordListAdapter.setItemClickListener(itemClickListener)
        }
        recordListAdapter.setItemLongClickListener(itemLongClickListener)
        rvRecord.adapter = recordListAdapter
    }

    private fun setButton() {
        btnAddRecord.setOnClickListener {
            val intent = Intent(ctx, AddSimpleRecordActivity::class.java)
            startActivity(intent)
        }
        btnEditDel.setOnClickListener {
            var checked = false
            val rdListIterator = rdList.iterator()
            while (rdListIterator.hasNext()) {
                val record = rdListIterator.next()
                if (record.checked) {
                    checked = true
                    rdListIterator.remove()
                    //db 삭제
                }
            }
            if (checked) {
                setAdapter(false)
            }
        }
        btnEditLock.setOnClickListener {
            var checked = false
            val rdListIterator = rdList.iterator()
            while (rdListIterator.hasNext()) {
                val record = rdListIterator.next()
                if (record.checked) {
                    //비밀번호 존재 여부 확인
                    checked = true
                    record.checked = false
                    if (!record.locked) {
                        record.locked = true
                    }
                    //db 잠금
                }
            }
            if (checked) {
                setAdapter(false)
            }
        }
        btnEditCancel.setOnClickListener {
            val rdListIterator = rdList.iterator()
            while (rdListIterator.hasNext()) {
                val record = rdListIterator.next()
                if (record.checked) {
                    record.checked = false
                }
            }
            setAdapter(false)
        }
        ivSetting.setOnClickListener {
            val intent = Intent(ctx, SettingActivity::class.java)
            startActivity(intent)
        }
    }
}
package com.yyj.frecord

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_record.*
import kotlinx.android.synthetic.main.dialog_record_setting.view.*
import kotlinx.android.synthetic.main.layout_edit.*

class RecordActivity : Fragment() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    lateinit var recordListAdapter: RecordListAdapter
    lateinit var ctx : Context
    lateinit var itemCheckListener: RecordListAdapter.OnItemClickListener
    private lateinit var itemClickListener: RecordListAdapter.OnItemClickListener
    lateinit var itemLongClickListener: RecordListAdapter.OnItemLongClickListener
    private lateinit var sharedPref : SharedPreferences
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
        dbHelper = DBHelper(ctx)
        db = dbHelper.writableDatabase
        sharedPref = ctx.getSharedPreferences("setting", Context.MODE_PRIVATE)
        tvUserName.text = sharedPref.getString("name", null)
        initList()
        if (rdList.size > 0) {
            llExplain.visibility = View.INVISIBLE
        }
        btnEditLock.visibility = View.VISIBLE
        setButton()
    }

    @SuppressLint("Range")
    private fun initList() {
        rdList.clear()
        val c : Cursor = db.rawQuery("SELECT * FROM ${DBHelper.REC_TABLE}", null)
        while (c.moveToNext()) {
            val id = c.getInt(c.getColumnIndex("_id"))
            val title = c.getString(c.getColumnIndex("title"))
            val date = c.getInt(c.getColumnIndex("date")) * 1000L
            val lock = c.getInt(c.getColumnIndex("lock"))
            val simple = c.getInt(c.getColumnIndex("simple"))
            rdList.add(RecordData(id, -1, title, null, date, lock, simple, null, null, null, null, false))
        }
        c.close()
        itemCheckListener = object : RecordListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val cbEdit = view.findViewById<CheckBox>(R.id.cbEditRecord)
                cbEdit.isChecked = !cbEdit.isChecked
            }
        }
        itemClickListener = object : RecordListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val intent : Intent = if (rdList[position].simple == 0) {
                    Intent(ctx, WriteRecordActivity::class.java)
                } else {
                    Intent(ctx, WriteSimpleRecordActivity::class.java)
                }
                intent.putExtra("id", rdList[position].id)
                startActivity(intent)
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
            val intent : Intent = if (sharedPref.getInt("mode", 0) == 1) {
                Intent(ctx, WriteRecordActivity::class.java)
            } else {
                Intent(ctx, WriteSimpleRecordActivity::class.java)
            }
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
                    db.execSQL("DELETE FROM ${DBHelper.REC_TABLE} WHERE _id = ${record.id}")
                }
            }
            if (checked) {
                setAdapter(false)
            }
        }
        btnEditLock.setOnClickListener {
            if (sharedPref.getString("password", null) == null) {
                val intent = Intent(ctx, PasswordSettingActivity::class.java)
                startActivity(intent)
            }
            else {
                var checked = false
                val rdListIterator = rdList.iterator()
                while (rdListIterator.hasNext()) {
                    val record = rdListIterator.next()
                    if (record.checked) {
                        checked = true
                        record.checked = false
                        if (record.locked == 0) {
                            record.locked = 1
                            db.execSQL("UPDATE ${DBHelper.REC_TABLE} SET ${DBHelper.REC_COL_LOCK}=1 WHERE _id=${record.id};")
                        }
                    }
                }
                if (checked) {
                    setAdapter(false)
                }
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
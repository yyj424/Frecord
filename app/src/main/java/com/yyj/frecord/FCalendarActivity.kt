package com.yyj.frecord

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_fcalendar.*
import kotlinx.android.synthetic.main.activity_record.layoutEdit
import kotlinx.android.synthetic.main.dialog_unlock.view.*
import kotlinx.android.synthetic.main.layout_bottom.*
import kotlinx.android.synthetic.main.layout_edit.*
import java.text.SimpleDateFormat
import java.util.*

class FCalendarActivity : Fragment() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    lateinit var recordListAdapter: RecordListAdapter
    lateinit var calendarAdapter: FCalendarAdapter
    lateinit var ctx : Context
    lateinit var dateClickListener: FCalendarAdapter.OnItemClickListener
    lateinit var itemCheckListener: RecordListAdapter.OnItemClickListener
    private lateinit var itemClickListener: RecordListAdapter.OnItemClickListener
    lateinit var itemLongClickListener: RecordListAdapter.OnItemLongClickListener
    private lateinit var sharedPref : SharedPreferences
    val calDataList = arrayListOf<CalendarData>()
    private val scoreMap = mutableMapOf<Int, CalendarData>()
    val rdList = arrayListOf<RecordData>()
    private val calendar: Calendar = Calendar.getInstance()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ctx = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_fcalendar, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        icMenu3.setImageResource(R.drawable.ic_selected_view)
        icMenu2.setImageResource(R.drawable.ic_not_selected_view)
        dbHelper = DBHelper(ctx)
        db = dbHelper.writableDatabase
        tvMonth.text = (calendar.get(Calendar.MONTH) + 1).toString()
        sharedPref = ctx.getSharedPreferences("setting", Context.MODE_PRIVATE)
        initCal()
        initRecord()
        setButton()
        btnEditLock.visibility = View.VISIBLE
    }

    private fun initCal() {
        setCal()
        dateClickListener = object : FCalendarAdapter.OnItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(view: View, position: Int) {
                if (calDataList[position].score != null) {
                    rvDayRecord.visibility = View.VISIBLE
                    getSelectedDateRecord(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH) + 1,
                        calDataList[position].date!!
                    )
                }
                else {
                    rvDayRecord.visibility = View.GONE
                }
            }
        }
        rvFCalendar.layoutManager = GridLayoutManager(ctx, 7)
        setCalAdapter()
    }
    private fun initRecord() {
        itemCheckListener = object : RecordListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                val cbEdit = view.findViewById<CheckBox>(R.id.cbEditRecord)
                cbEdit.isChecked = !cbEdit.isChecked
            }
        }
        itemClickListener = object : RecordListAdapter.OnItemClickListener {
            override fun onClick(view: View, position: Int) {
                if (rdList[position].locked == 1) {
                    alertDialog(position)
                }
                else {
                    val intent : Intent = if (rdList[position].simple == 0) {
                        Intent(ctx, WriteRecordActivity::class.java)
                    } else {
                        Intent(ctx, WriteSimpleRecordActivity::class.java)
                    }
                    intent.putExtra("record", rdList[position])
                    startActivity(intent)
                }
            }
        }
        itemLongClickListener = object : RecordListAdapter.OnItemLongClickListener {
            override fun onLongClick(view: View, position: Int): Boolean {
                if (layoutEdit.visibility == View.GONE) {
                    rdList[position].checked = true
                    setRecordAdapter(true)
                }
                view.findViewById<CheckBox>(R.id.cbEditRecord).isChecked = true
                return true
            }
        }
        rvDayRecord.layoutManager = LinearLayoutManager(ctx, LinearLayoutManager.VERTICAL, false)
        setRecordAdapter(false)
    }

    @SuppressLint("Range")
    private fun setCal() {
        calDataList.clear()
        scoreMap.clear()
        calendar.set(Calendar.DATE, 1)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK)
        var date = 1
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val c : Cursor = db.rawQuery("SELECT _id, date, score FROM ${DBHelper.REC_TABLE}", null)
        while (c.moveToNext()) {
            val id = c.getInt(c.getColumnIndex(DBHelper.REC_COL_ID))
            val recordDate = c.getInt(c.getColumnIndex(DBHelper.REC_COL_DATE)) * 1000L
            val score = c.getInt(c.getColumnIndex(DBHelper.REC_COL_SCORE))
            val date = dateFormat.format(recordDate).split(".")
            if (date[0].toInt() == calendar.get(Calendar.YEAR) && date[1].toInt() == (calendar.get(Calendar.MONTH) + 1)) {
                scoreMap[id] = CalendarData(id, date[2].toInt(), score)
            }
        }
        for (i in 1..(calendar.getActualMaximum(Calendar.DATE) + firstDayOfMonth)) {
            if (i >= firstDayOfMonth && date <= calendar.getActualMaximum(Calendar.DATE)) {
                val isScore = scoreMap.filter { (k , v) ->
                    v.date == date
                }
                if (isScore.isNotEmpty()) {
                    val calData = isScore.values.iterator().next()
                    calDataList.add(CalendarData(calData.id, date, calData.score))
                }
                else {
                    calDataList.add(CalendarData(null, date, null))
                }
                date++
            }
            else {
                calDataList.add(CalendarData(null, null, null))
            }
        }
    }

    @SuppressLint("Range", "NotifyDataSetChanged")
    private fun getSelectedDateRecord(year: Int, month: Int, day: Int) {
        rdList.clear()
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        val c : Cursor = db.rawQuery("SELECT * FROM ${DBHelper.REC_TABLE}", null)
        while (c.moveToNext()) {
            val id = c.getInt(c.getColumnIndex(DBHelper.REC_COL_ID))
            val score = c.getInt(c.getColumnIndex(DBHelper.REC_COL_SCORE))
            val title = c.getString(c.getColumnIndex(DBHelper.REC_COL_TITLE))
            val content = c.getString(c.getColumnIndex(DBHelper.REC_COL_CONTENT))
            val recordDate = c.getInt(c.getColumnIndex(DBHelper.REC_COL_DATE)) * 1000L
            val lock = c.getInt(c.getColumnIndex(DBHelper.REC_COL_LOCK))
            val simple = c.getInt(c.getColumnIndex(DBHelper.REC_COL_SIMPLE))
            val where = c.getString(c.getColumnIndex(DBHelper.REC_COL_WHR))
            val what = c.getString(c.getColumnIndex(DBHelper.REC_COL_WHAT))
            val feeling = c.getString(c.getColumnIndex(DBHelper.REC_COL_FEELING))
            val why = c.getString(c.getColumnIndex(DBHelper.REC_COL_WHY))

            val date = dateFormat.format(recordDate).split(".")
            if (date[0].toInt() == year && date[1].toInt() == month && date[2].toInt() == day) {
                rdList.add(RecordData(id, score, title, content, recordDate, lock, simple, where, what, feeling, why, false))
            }
        }
        c.close()
        Log.d("yyjLog", ""+ rdList)
        rdList.sortBy { recordData -> recordData.date }
        recordListAdapter.notifyDataSetChanged()
    }

    private fun alertDialog(position : Int) {
        var tryUnlock = 0
        val builder = AlertDialog.Builder(ctx)
        val view = LayoutInflater.from(ctx).inflate(R.layout.dialog_unlock, null)
        view.tvUnlockHint.text = sharedPref.getString("hint", null)
        val dialog = builder.setView(view)
            .setCancelable(false)
            .create()
        dialog.show()
        view.btnUnlockCancel.setOnClickListener {
            dialog.dismiss()
        }
        view.btnUnlockConfirm.setOnClickListener {
            tryUnlock++
            if (view.etUnlockPw.text.toString() == sharedPref.getString(
                    "password",
                    null
                )
            ) {
                dialog.dismiss()
                val intent: Intent = if (rdList[position].simple == 0) {
                    Intent(ctx, WriteRecordActivity::class.java)
                } else {
                    Intent(ctx, WriteSimpleRecordActivity::class.java)
                }
                intent.putExtra("record", rdList[position])
                startActivity(intent)
            } else {
                if (tryUnlock == 5) {
                    view.llUnlockHint.visibility = View.VISIBLE
                }
                view.tvUnlockCaution.visibility = View.VISIBLE
            }
        }
    }

    private fun setCalAdapter() {
        calendarAdapter = FCalendarAdapter(ctx, calDataList)
        calendarAdapter.setItemClickListener(dateClickListener)
        rvFCalendar.adapter = calendarAdapter
    }

    private fun setRecordAdapter(edit : Boolean) {
        recordListAdapter = RecordListAdapter(ctx, rdList, edit)
        if (edit) {
            layoutEdit.visibility = View.VISIBLE
            recordListAdapter.setItemClickListener(itemCheckListener)
        }
        else {
            layoutEdit.visibility = View.GONE
            recordListAdapter.setItemClickListener(itemClickListener)
        }
        recordListAdapter.setItemLongClickListener(itemLongClickListener)
        rvDayRecord.adapter = recordListAdapter
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun setButton() {
        btnPrevious.setOnClickListener {
            calendar.add(Calendar.MONTH, -1)
            setCal()
            calendarAdapter.notifyDataSetChanged()
            tvMonth.text = (calendar.get(Calendar.MONTH) + 1).toString()
        }
        btnNext.setOnClickListener {
            calendar.add(Calendar.MONTH, 1)
            setCal()
            calendarAdapter.notifyDataSetChanged()
            tvMonth.text = (calendar.get(Calendar.MONTH) + 1).toString()
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
                setRecordAdapter(false)
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
                    setRecordAdapter(false)
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
            setRecordAdapter(false)
        }
    }
}
package com.yyj.frecord

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_simple_record.*
import kotlinx.android.synthetic.main.dialog_record_setting.view.*

class WriteSimpleRecordActivity : AppCompatActivity() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    private lateinit var dialog : AlertDialog
    private var record = RecordData(-1, -1, null, null, -1, 0, 1, null, null, null, null, false)
    private var locked = 0
    private var place = 0
    private var feeling = 0
    private var backPressedTime : Long = 0
    private var inserted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_simple_record)
        dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase
        setSeekBar()
        setSpinners()
        if (intent.hasExtra("record")) {
            record = intent.getParcelableExtra<RecordData>("record")!!
            getRecord()
        }
        setSettingDialog()

        ivSimpleRecordSetting.setOnClickListener {
            dialog.show()
        }
    }

    private fun setSeekBar() {
        seekBarInSimple.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb4)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun setSpinners() {
        placeSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.placeList, android.R.layout.simple_list_item_1)
        placeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos == 0) {
                    etSimpleRecordWhere.visibility = View.VISIBLE
                }
                else {
                    etSimpleRecordWhere.visibility = View.GONE
                }
                place = pos
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        feelingSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.feelingList, android.R.layout.simple_list_item_1)
        feelingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos == 0) {
                    etSimpleRecordFeeling.visibility = View.VISIBLE
                }
                else {
                    etSimpleRecordFeeling.visibility = View.GONE
                }
                feeling = pos
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setSettingDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_record_setting, null)
        val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                locked = 1
                view.ivLockSetting.setImageResource(R.drawable.ic_lock)
            }
        }
        view.tvModeSetting.text = "자유기록"
        view.btnChangeMode.setOnClickListener {
            dialog.dismiss()
            if (record.id != -1) {
                db.execSQL("DELETE FROM ${DBHelper.REC_TABLE} WHERE _id = ${record.id}")
            }
            db.close()
            val intent = Intent(this, WriteRecordActivity::class.java)
            startActivity(intent)
            finish()
        }
        view.btnLockSetting.setOnClickListener {
            if (locked == 0) {
                val sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
                if (sharedPref.getString("password", null) == null) {
                    val intent = Intent(this, PasswordSettingActivity::class.java)
                    intent.putExtra("from", "record")
                    getResult.launch(intent)
                }
                else {
                    view.ivLockSetting.setImageResource(R.drawable.ic_lock)
                }
                locked = 1
            }
            else {
                view.ivLockSetting.setImageResource(R.drawable.ic_unlock)
                locked = 0
            }
        }
        if (record.id != -1) {
            if (record.locked == 1) {
                locked = 1
                view.ivLockSetting.setImageResource(R.drawable.ic_lock)
            }
            view.btnDeleteRecord.visibility = View.VISIBLE
            view.btnDeleteRecord.setOnClickListener {
                db.execSQL("DELETE FROM ${DBHelper.REC_TABLE} WHERE _id = ${record.id}")
                db.close()
                finish()
            }
        }
        builder.setView(view)
        dialog = builder.create()
    }

    override fun onBackPressed() {
        Log.d("yyjLog", "back")
        if (System.currentTimeMillis() - backPressedTime > 5000) {
            backPressedTime = System.currentTimeMillis()
            saveRecord()
            Log.d("yyjLog", "inserted" + inserted)
        }
        else {
            db.close()
            finish()
        }
    }

    private fun saveRecord() {
        val values = ContentValues()
        values.put(DBHelper.REC_COL_SCORE, seekBarInSimple.progress)
        values.put(DBHelper.REC_COL_TITLE, etSimpleRecordTitle.text.toString())
        if (place != 0) {
            values.put(DBHelper.REC_COL_WHR, place.toString())
        }
        else {
            values.put(DBHelper.REC_COL_WHR, "0" + etSimpleRecordWhere.text.toString())
        }
        values.put(DBHelper.REC_COL_WHAT, etSimpleRecordWhat.text.toString())
        if (feeling != 0) {
            values.put(DBHelper.REC_COL_FEELING, feeling.toString())
        }
        else {
            values.put(DBHelper.REC_COL_FEELING, "0" + etSimpleRecordFeeling.text.toString())
        }
        values.put(DBHelper.REC_COL_WHY, etSimpleRecordWhy.text.toString())
        values.put(DBHelper.REC_COL_DATE, System.currentTimeMillis() / 1000L)
        values.put(DBHelper.REC_COL_LOCK, locked)
        values.put(DBHelper.REC_COL_SIMPLE, 1)

        if (record.id != -1 || inserted) {
            Log.d("yyjLog", "update")
            if (checkUpdate()) {
                val whereClause = "_id=?"
                val whereArgs = arrayOf(record.id.toString())
                db.update(DBHelper.REC_TABLE, values, whereClause, whereArgs)
            }
        }
        else {
            Log.d("yyjLog", "insert")
            db.insert(DBHelper.REC_TABLE, null, values)
            inserted = true
            record.score = seekBarInSimple.progress
            record.title = etSimpleRecordTitle.text.toString()
            record.where = etSimpleRecordWhere.text.toString()
            record.what = etSimpleRecordWhat.text.toString()
            record.feeling = etSimpleRecordFeeling.text.toString()
            record.why = etSimpleRecordWhy.text.toString()
            record.locked = locked
        }
    }

    private fun getRecord() {
        seekBarInSimple.progress = record.score
        when (record.score) {
            0 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb0)
            1 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb1)
            2 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb2)
            3 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb3)
            4 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@WriteSimpleRecordActivity, R.drawable.ic_thumb4)
        }
        etSimpleRecordTitle.setText(record.title)
        if (record.where!![0] == '0'){
            val token = record.where!!.split('0')
            etSimpleRecordWhere.setText(token[1])
        }
        else {
            placeSpinner.setSelection(record.where!!.toInt())
        }
        etSimpleRecordWhat.setText(record.what)
        if (record.feeling!![0] == '0'){
            val token = record.feeling!!.split('0')
            etSimpleRecordFeeling.setText(token[1])
        }
        else {
            feelingSpinner.setSelection(record.feeling!!.toInt())
        }
        etSimpleRecordWhy.setText(record.why)
    }

    private fun checkUpdate() : Boolean {
        if (record.score != seekBarInSimple.progress) return true
        if (record.title != etSimpleRecordTitle.text.toString()) return true
        if (record.where != etSimpleRecordWhere.text.toString()) return true
        if (record.what != etSimpleRecordWhat.text.toString()) return true
        if (record.feeling != etSimpleRecordFeeling.text.toString()) return true
        if (record.why != etSimpleRecordWhy.text.toString()) return true
        if (record.locked != locked) return true
        return false
    }
}
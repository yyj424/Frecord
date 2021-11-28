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
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_record.*
import kotlinx.android.synthetic.main.dialog_record_setting.view.*

class WriteRecordActivity : AppCompatActivity() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    private lateinit var dialog : AlertDialog
    private var record = RecordData(-1, -1, null, null, -1, 0, 0, null, null, null, null, false)
    private var locked = 0
    private var backPressedTime : Long = 0
    private var inserted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)
        dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase
        if (intent.hasExtra("record")) {
            record = intent.getParcelableExtra<RecordData>("record")!!
            getRecord()
        }
        setSeekBar()
        setSettingDialog()
        ivRecordSetting.setOnClickListener {
            dialog.show()
        }
    }

    private fun setSeekBar() {
        seekBarInFree.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb4)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
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
        view.tvModeSetting.text = "간편기록"
        view.btnChangeMode.setOnClickListener {
            dialog.dismiss()
            db.close()
            val intent = Intent(this, WriteSimpleRecordActivity::class.java)
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
            Log.d("yyjLog", "saved!!!!")
        }
        else {
            db.close()
            finish()
        }
    }

    private fun saveRecord() {
        val values = ContentValues()
        values.put(DBHelper.REC_COL_SCORE, seekBarInFree.progress)
        values.put(DBHelper.REC_COL_TITLE, etRecordTitle.text.toString())
        values.put(DBHelper.REC_COL_CONTENT, etRecordContent.text.toString())
        values.put(DBHelper.REC_COL_DATE, System.currentTimeMillis() / 1000L)
        values.put(DBHelper.REC_COL_LOCK, locked)
        values.put(DBHelper.REC_COL_SIMPLE, 0)

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
            record.score = seekBarInFree.progress
            record.title = etRecordTitle.text.toString()
            record.content = etRecordContent.text.toString()
            record.locked = locked
        }
    }

    @SuppressLint("Range")
    private fun getRecord() {
        seekBarInFree.progress = record.score
        when (record.score) {
            0 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb0)
            1 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb1)
            2 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb2)
            3 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb3)
            4 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@WriteRecordActivity, R.drawable.ic_thumb4)
        }
        etRecordTitle.setText(record.title)
        etRecordContent.setText(record.content)
        etRecordContent.setSelection(etRecordContent.text.length)
    }

    private fun checkUpdate() : Boolean {
        if (record.score != seekBarInFree.progress) return true
        if (record.title != etRecordTitle.text.toString()) return true
        if (record.content != etRecordContent.text.toString()) return true
        if (record.locked != locked) return true
        return false
    }
}
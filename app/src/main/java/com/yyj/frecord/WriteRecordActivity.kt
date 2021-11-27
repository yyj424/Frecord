package com.yyj.frecord

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_record.*
import kotlinx.android.synthetic.main.dialog_record_setting.view.*

class WriteRecordActivity : AppCompatActivity() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    private lateinit var dialog : AlertDialog
    private var locked = 0
    private var backPressedTime : Long = 0
    private val id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)
        dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase
        if (intent.hasExtra("id")) {
            intent.getIntExtra("id", -1)
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
                view.ivLockSetting.setImageResource(R.drawable.ic_lock)
            }
        }
        view.tvModeSetting.text = "간편기록"
        view.btnChangeMode.setOnClickListener {
            dialog.dismiss()
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
        view.btnDeleteRecord.setOnClickListener {

        }
        builder.setView(view)
        dialog = builder.create()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime > 2000) {
            backPressedTime = System.currentTimeMillis()
            saveRecord() //insert 부분 해결
        }
        else {
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

        if (id == -1) {
            if (checkUpdate()) {
                val whereClause = "_id=?"
                val whereArgs = arrayOf(id.toString())
                db.update(DBHelper.REC_TABLE, values, whereClause, whereArgs)
            }
            else {
                db.insert(DBHelper.REC_TABLE, null, values)
            }
        }
    }

    private fun getRecord() {

    }

    private fun checkUpdate() : Boolean {
        return false
    }
}
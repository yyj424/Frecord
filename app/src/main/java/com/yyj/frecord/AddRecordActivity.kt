package com.yyj.frecord

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_record.*
import kotlinx.android.synthetic.main.dialog_record_setting.view.*

class AddRecordActivity : AppCompatActivity() {
    private lateinit var dialog : AlertDialog
    private var locked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)
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
                    0 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb4)
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
            val intent = Intent(this, AddSimpleRecordActivity::class.java)
            startActivity(intent)
            finish()
        }
        view.btnLockSetting.setOnClickListener {
            locked = if (!locked) {
                val sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
                if (sharedPref.getString("password", null) == null) {
                    val intent = Intent(this, PasswordSettingActivity::class.java)
                    intent.putExtra("from", "record")
                    getResult.launch(intent)
                }
                else {
                    view.ivLockSetting.setImageResource(R.drawable.ic_lock)
                }
                true
            } else {
                view.ivLockSetting.setImageResource(R.drawable.ic_unlock)
                false
            }
        }
        view.btnDeleteRecord.setOnClickListener {

        }
        builder.setView(view)
        dialog = builder.create()
    }
}
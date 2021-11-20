package com.yyj.frecord

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_simple_record.*
import kotlinx.android.synthetic.main.dialog_record_setting.view.*

class AddSimpleRecordActivity : AppCompatActivity() {
    private lateinit var dialog : AlertDialog
    private var locked = false
    private var place: String? = null
    private var feeling: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_simple_record)
        setSeekBar()
        setSpinners()
        setSettingDialog()

        ivSimpleRecordSetting.setOnClickListener {
            dialog.show()
        }
    }

    private fun setSeekBar() {
        seekBarInSimple.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb4)
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
                    etSimpleRecordWhere.setText("")
                    etSimpleRecordWhere.visibility = View.VISIBLE
                    place = null
                }
                else {
                    etSimpleRecordWhere.visibility = View.GONE
                    place = placeSpinner.selectedItem.toString()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        feelingSpinner.adapter = ArrayAdapter.createFromResource(this, R.array.feelingList, android.R.layout.simple_list_item_1)
        feelingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, pos: Int, p3: Long) {
                if (pos == 0) {
                    etSimpleRecordFeeling.setText("")
                    etSimpleRecordFeeling.visibility = View.VISIBLE
                    feeling = null
                }
                else {
                    etSimpleRecordFeeling.visibility = View.GONE
                    feeling = feelingSpinner.selectedItem.toString()
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun setSettingDialog() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_record_setting, null)
        view.tvModeSetting.text = "자유기록"
        view.btnChangeMode.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, AddRecordActivity::class.java)
            startActivity(intent)
            finish()
        }
        view.btnLockSetting.setOnClickListener {
            locked = if (!locked) {
                view.ivLockSetting.setImageResource(R.drawable.ic_lock)
                true
            } else {
                view.ivLockSetting.setImageResource(R.drawable.ic_unlock)
                false
            }
            dialog.dismiss()
        }
        view.btnDeleteRecord.setOnClickListener {
            dialog.dismiss()
        }
        builder.setView(view)
        dialog = builder.create()
    }
}
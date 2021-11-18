package com.yyj.frecord

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_simple_record.*

class AddSimpleRecordActivity : AppCompatActivity() {
    private var feelingScore = 2
    private var place: String? = null
    private var feeling: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_simple_record)

        val title = intent.getStringExtra("title")
        feelingScore = intent.getIntExtra("feelingScore", 2)

        if (title != null) {
            etSimpleRecordTitle.setText(title)
        }

        setSeekBar()
        setSpinners()

        ivSimpleRecordSetting.setOnClickListener {
            val intent = Intent(this, AddRecordActivity::class.java)
            intent.putExtra("title", etSimpleRecordTitle.text.toString())
            intent.putExtra("feelingScore", feelingScore)
            startActivity(intent)
            finish()
            //dialog로 변경
        }
    }

    private fun setSeekBar() {
        seekBarInSimple.progress = feelingScore
        seekBarInSimple.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb4)
                }
                feelingScore = p1
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
}
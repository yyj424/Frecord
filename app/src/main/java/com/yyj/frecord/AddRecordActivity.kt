package com.yyj.frecord

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_record.*

class AddRecordActivity : AppCompatActivity() {
    private var feelingScore = 2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        val title = intent.getStringExtra("title")
        feelingScore = intent.getIntExtra("feelingScore", 2)

        if (title != null) {
            etRecordTitle.setText(title)
        }
        setSeekBar()

        ivRecordSetting.setOnClickListener {
            val intent = Intent(this, AddSimpleRecordActivity::class.java)
            intent.putExtra("title", etRecordTitle.text.toString())
            intent.putExtra("feelingScore", feelingScore)
            startActivity(intent)
            finish()
            //dialog로 변경
        }
    }

    private fun setSeekBar() {
        seekBarInFree.progress = feelingScore
        seekBarInFree.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInFree.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb4)
                }
                feelingScore = p1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }
}
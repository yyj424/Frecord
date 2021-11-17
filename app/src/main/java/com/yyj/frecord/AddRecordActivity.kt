package com.yyj.frecord

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_record.*

class AddRecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_record)

        seekBar.setOnSeekBarChangeListener(object:SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBar.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBar.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBar.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBar.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBar.thumb = ContextCompat.getDrawable(this@AddRecordActivity, R.drawable.ic_thumb4)
                }
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }
}
package com.yyj.frecord

import android.content.Intent
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_add_simple_record.*

class AddSimpleRecordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_simple_record)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        var emotion = intent.getIntExtra("emotion", 2)

        if (title != null) {

        }
        if (content != null) {

        }
        seekBarInSimple.progress = emotion

        tvChangeFreeMode.setOnClickListener {
            val intent = Intent(this, AddRecordActivity::class.java)
//            intent.putExtra("title", )
            intent.putExtra("emotion", emotion)
//            intent.putExtra("content", )
            startActivity(intent)
            finish()
        }

        seekBarInSimple.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    0 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb0)
                    1 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb1)
                    2 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb2)
                    3 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb3)
                    4 -> seekBarInSimple.thumb = ContextCompat.getDrawable(this@AddSimpleRecordActivity, R.drawable.ic_thumb4)
                }
                emotion = p1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }
}
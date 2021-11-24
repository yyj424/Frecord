package com.yyj.frecord

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_default.*

class DefaultSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_default)

        val sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
        etSettingUserName.setText(sharedPref.getString("name", ""))
        var mode = sharedPref.getInt("mode", 0)
        if (mode == 0) {
            rbtnMode0.isChecked = true
        }
        else {
            rbtnMode1.isChecked = true
        }

        rgSettingRecordMode.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rbtnMode0 -> mode = 0
                R.id.rbtnMode1 -> mode = 1
            }
        }

        btnSaveSetting.setOnClickListener {
            if (!TextUtils.isEmpty(etSettingUserName.text.toString().trim()))
            {
                sharedPref.edit().apply {
                    putString("name", etSettingUserName.text.toString().trim())
                    putInt("mode", mode)
                    apply()
                }
                Toast.makeText(this, "저장되었습니다", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
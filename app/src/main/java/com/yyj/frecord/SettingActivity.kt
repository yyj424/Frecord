package com.yyj.frecord

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class SettingActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
    }

    override fun onClick(p0: View) {
        when (p0.id) {
            R.id.tvHelp -> {
                val intent = Intent(this, HelpActivity::class.java)
                startActivity(intent)
            }
            R.id.tvDefaultSetting -> {
                val intent = Intent(this, DefaultSettingActivity::class.java)
                startActivity(intent)
            }
            R.id.tvPwSetting -> {
                val intent = Intent(this, PasswordSettingActivity::class.java)
                startActivity(intent)
            }
            R.id.tvLicense -> {
                val intent = Intent(this, LicenseActivity::class.java)
                startActivity(intent)
            }
            R.id.tvHelpDev -> {
//                val intent = Intent(this, ::class.java)
//                startActivity(intent)
            }
        }
    }
}
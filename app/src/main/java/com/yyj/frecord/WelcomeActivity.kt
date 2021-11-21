package com.yyj.frecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val sharedPref = getSharedPreferences("user", Context.MODE_PRIVATE)
        if (sharedPref.getString("name", null) != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnConfirmUserName.setOnClickListener {
            if (!TextUtils.isEmpty(etUserName.text.toString().trim()))
            {
                sharedPref.edit().apply{
                    putString("name", etUserName.text.toString().trim())
                    apply()
                }
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                etUserName.text = null
            }
        }
    }
}
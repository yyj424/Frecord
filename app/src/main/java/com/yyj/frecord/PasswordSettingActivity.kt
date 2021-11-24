package com.yyj.frecord

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_password.*

class PasswordSettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password)

        val sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
        if (sharedPref.getString("password", null) != null) {
            val intent = Intent(this, UnlockActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnSavePassword.setOnClickListener {
            if (!TextUtils.isEmpty(etPassword.text.toString().trim()) && !TextUtils.isEmpty(etConfirmPassword.text.toString().trim()) && !TextUtils.isEmpty(etPwHint.text.toString().trim()))
            {
                if (etPassword.text.toString() == etConfirmPassword.text.toString()) {
                    sharedPref.edit().apply {
                        putString("password", etPassword.text.toString().trim())
                        putString("hint", etPwHint.text.toString().trim())
                        apply()
                    }
                    finish()
                }
                else {
                    llPwCaution.visibility = View.VISIBLE
                }
            }
            else {
                if (TextUtils.isEmpty(etPassword.text.toString().trim()) || TextUtils.isEmpty(etConfirmPassword.text.toString())) {
                    llPwCaution.visibility = View.VISIBLE
                }
                if (TextUtils.isEmpty(etPwHint.text.toString().trim())) {
                    llHintCaution.visibility = View.VISIBLE
                }
            }
        }
    }
}
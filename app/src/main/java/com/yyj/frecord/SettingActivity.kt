package com.yyj.frecord

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.dialog_unlock.*
import kotlinx.android.synthetic.main.dialog_unlock.view.*

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
                val sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
                if (sharedPref.getString("password", null) != null) {
                    var tryUnlock = 0
                    val builder = AlertDialog.Builder(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog_unlock, null)
                    view.tvUnlockHint.text = sharedPref.getString("hint", null)
                    val dialog = builder.setView(view)
                        .setCancelable(false)
                        .create()
                    dialog.show()
                    view.btnUnlockCancel.setOnClickListener {
                        dialog.dismiss()
                    }
                    view.btnUnlockConfirm.setOnClickListener {
                        tryUnlock++
                        if (view.etUnlockPw.text.toString() == sharedPref.getString("password", null)) {
                            dialog.dismiss()
                            val intent = Intent(this, PasswordSettingActivity::class.java)
                            startActivity(intent)
                        }
                        else {
                            if (tryUnlock == 5) {
                                view.llUnlockHint.visibility = View.VISIBLE
                            }
                            view.tvUnlockCaution.visibility = View.VISIBLE
                        }
                    }
                }
                else {
                    val intent = Intent(this, PasswordSettingActivity::class.java)
                    startActivity(intent)
                }
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
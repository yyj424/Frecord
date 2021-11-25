package com.yyj.frecord

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.activity_record.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewPagerFragmentAdapter : ViewPagerAdapter
    private lateinit var sharedPref : SharedPreferences
    private lateinit var name : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewPagerFragmentAdapter = ViewPagerAdapter(this)
        viewPager.adapter = viewPagerFragmentAdapter
        viewPager.setCurrentItem(1, false)

        sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
        name = sharedPref.getString("name", null).toString()
    }

    override fun onRestart() {
        super.onRestart()
        if (name != sharedPref.getString("name", null).toString()) {
            name = sharedPref.getString("name", null).toString()
            val messageFragment = supportFragmentManager.findFragmentByTag("f0") as MessageActivity
            val recordFragment = supportFragmentManager.findFragmentByTag("f1") as RecordActivity
            messageFragment.tvToUserName.text = name
            recordFragment.tvUserName.text = name
        }
    }
}
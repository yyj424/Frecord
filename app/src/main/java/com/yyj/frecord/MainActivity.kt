package com.yyj.frecord

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_fcalendar.*
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

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        val messageFragment = supportFragmentManager.findFragmentByTag("f0")
        val recordFragment = supportFragmentManager.findFragmentByTag("f1")
        val calendarFragment = supportFragmentManager.findFragmentByTag("f2")
        if (recordFragment != null) {
            recordFragment as RecordActivity
            recordFragment.setListData()
            recordFragment.recordListAdapter.notifyDataSetChanged()
            if (recordFragment.rdList.size > 0) {
                recordFragment.llExplain.visibility = View.INVISIBLE
            }
            else {
                recordFragment.llExplain.visibility = View.VISIBLE
            }
        }
        if (messageFragment != null) {
            messageFragment as MessageActivity
            messageFragment.setListData()
            messageFragment.msgListAdapter.notifyDataSetChanged()
            if (messageFragment.msgList.size > 0) {
                messageFragment.tvMsgExplain.visibility = View.INVISIBLE
            }
            else {
                messageFragment.tvMsgExplain.visibility = View.VISIBLE
            }
        }
        if (calendarFragment != null) {
            calendarFragment as FCalendarActivity
            if (calendarFragment.rvDayRecord.visibility == View.VISIBLE) {
                val date = calendarFragment.selectedDate.split(".")
                calendarFragment.getSelectedDateRecord(date[0].toInt(), date[1].toInt(), date[2].toInt())
            }
        }
        if (name != sharedPref.getString("name", null).toString()) {
            name = sharedPref.getString("name", null).toString()
            recordFragment?.tvUserName?.text = name
            messageFragment?.tvToUserName?.text = name
        }
    }
}
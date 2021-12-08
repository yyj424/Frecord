package com.yyj.frecord

import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_message.*
import kotlinx.android.synthetic.main.activity_add_record.*
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import java.text.SimpleDateFormat
import java.util.*


class AddMessageActivity : AppCompatActivity() {
    private lateinit var dbHelper : DBHelper
    private lateinit var db : SQLiteDatabase
    private lateinit var dialog : AlertDialog
    private lateinit var intent : PendingIntent
    private val alarmCalendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    private var calDate = arrayOf(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
        today.get(Calendar.DAY_OF_MONTH), today.get(Calendar.HOUR), today.get(Calendar.MINUTE))
    private var user = 0 //0:~아/야, 1:~, 2.이름 없음
    private lateinit var randomMsg : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_message)
        dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase
        val sharedPref = this.getSharedPreferences("setting", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("name", "")
        tvSendMsgUserName.text = userName
        setAlertCalendar()
        setCheckBox()

        llSendMsgDate.setOnClickListener {
            dialog.show()
        }
        timePicker.setOnTimeChangedListener { _, h, m ->
            calDate[3] = h
            calDate[4] = m
        }
        rgUserName.setOnCheckedChangeListener { _, i ->
            when (i) {
                R.id.rbtn0 -> {
                    user = 0
                    tvSendMsgUserName.visibility = View.VISIBLE
                }
                R.id.rbtn1 -> {
                    user = 1
                    tvSendMsgUserName.visibility = View.VISIBLE
                }
                R.id.rbtn2 -> {
                    user = 2
                    tvSendMsgUserName.visibility = View.GONE
                }
            }
        }
        btnSaveMessage.setOnClickListener {
            if (TextUtils.isEmpty(etSendMsgContent.text.toString().trim()))
            {
                Toast.makeText(this, "메시지를 입력해 주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                if (userName != null) {
                    alarmCalendar.set(calDate[0], calDate[1], calDate[2], calDate[3], calDate[4], 0)
                    var isRandMsg = false
                    if (checkBox1.isChecked) {
                        isRandMsg = true
                        setRandom(1)
                    }
                    if (checkBox2.isChecked) {
                        setRandom(2)
                    }
                    if (alarmCalendar.timeInMillis > System.currentTimeMillis()) {
                        val message = setSendMsg(userName, isRandMsg)
                        val req = System.currentTimeMillis().toInt()
                        setAlarm(message, req)
                        saveMessage(message, req)
                        db.close()
                        finish()
                    }
                    else {
                        Toast.makeText(this, "현재 시간 이후로 설정해 주세요", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setAlertCalendar() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_datepicker, null)
        val calView = view.calendarView
        val dateFormat = SimpleDateFormat("yyyy. MM. dd")
        val selectedDate = Calendar.getInstance()
        tvSendMsgDate.text = dateFormat.format(calView.date)
        calView.minDate = System.currentTimeMillis() - 1000
        calView.setOnDateChangeListener { _, y, m, d ->
            calDate[0] = y
            calDate[1] = m
            calDate[2] = d
            selectedDate.set(y, m, d)
            calView.date = selectedDate.timeInMillis
            tvSendMsgDate.text = dateFormat.format(calView.date)
            dialog.dismiss()
        }
        builder.setView(view)
        dialog = builder.create()
    }

    private fun setAlarm(message: String, req: Int) {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        intent = Intent(this, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("message", message)
            intent.putExtra("id", req)
            PendingIntent.getBroadcast(this, req, intent, 0) //flag 확인
        }
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            intent)
        createNotificationChannel(req.toString())
    }

    private fun createNotificationChannel(id: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setSendMsg(name: String, rand: Boolean): String {
        var to = ""
        if (user == 0) {
            to = isUserNameKorean(name)
        }
        else if (user == 1) {
            to = "$name, "
        }
        return if (rand) to + randomMsg else to + etSendMsgContent.text.toString()
    }

    private fun isUserNameKorean(name: String): String {
        val last = name[name.length - 1]
        if (last < 0xAC00.toChar() || last > 0xD7A3.toChar()) {
            return name + "아, "
        }
        return if ((last - 0xAC00.toChar()) % 28 > 0) {
            name + "아, "
        } else {
            name + "야, "
        }
    }

    private fun setCheckBox() {
        var tempUser = 0
        var tempContent = ""
        checkBox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                tempUser = user
                tempContent = etSendMsgContent.text.toString()
                llSendMsgName.visibility = View.GONE
                etSendMsgContent.setText("당신의 감정을 위한 메시지를 보내드려요.")
                etSendMsgContent.isEnabled = false
            }
            else {
                user = tempUser
                llSendMsgName.visibility = View.VISIBLE
                etSendMsgContent.setText(tempContent)
                etSendMsgContent.isEnabled = true
            }
        }
        checkBox2.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                llSendMsgDate.visibility = View.GONE
            }
            else {
                llSendMsgDate.visibility = View.VISIBLE
            }
        }
    }

    private fun setRandom(cb: Int) {
        if (cb == 1) { //메시지
            user = (0..2).random()
            randomMsg = "random" // 임시 랜덤 메시지
        }
        else { //날짜
            alarmCalendar.add(Calendar.DAY_OF_MONTH, (1..30).random())
        }
    }

    private fun saveMessage(content: String, req: Int) {
        val values = ContentValues()
        values.put(DBHelper.MSG_COL_CONTENT, content)
        values.put(DBHelper.MSG_COL_DATE, alarmCalendar.timeInMillis / 1000L)
        values.put(DBHelper.MSG_COL_REQ, req)
        db.insert(DBHelper.MSG_TABLE, null, values)
    }
}
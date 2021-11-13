package com.yyj.frecord

import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_message.*
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import java.util.*


class AddMessageActivity : AppCompatActivity() {
    private val channelId = "msg"
    private lateinit var dialog : AlertDialog
    private lateinit var intent : PendingIntent
    private val alarmCalendar = Calendar.getInstance()
    private lateinit var message : MessageData
    private var calDate = arrayOf(alarmCalendar.get(Calendar.YEAR), alarmCalendar.get(Calendar.MONTH),
        alarmCalendar.get(Calendar.DAY_OF_MONTH), alarmCalendar.get(Calendar.HOUR), alarmCalendar.get(Calendar.MINUTE))
    private var user = 0 //0:님 1:아/야 2.이름 없음
    //tone 0:공손 1:친근

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_message)
        val sharedPref = this.getSharedPreferences("user", Context.MODE_PRIVATE)
        val userName = sharedPref.getString("name", "")
        tvSendMsgUserName.text = userName
        alertCalendar()
        createNotificationChannel()

        btnPickDate.setOnClickListener {
            dialog.show()
        }
        timePicker.setOnTimeChangedListener { _, h, m ->
            calDate[3] = h
            calDate[4] = m
            Log.d("yyjLog", "time: " + calDate[3] + ", " + calDate[4])
        }
        rgUserName.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rbtn0 -> user = 0
                R.id.rbtn1 -> user = 1
                R.id.rbtn2 -> user = 2
            }
        }
        btnSaveMessage.setOnClickListener {
            message.id = 0 //임시 id
            message.content = etSendMsgContent.text.toString()
            if (userName != null) {
                setSendMsgName(userName)
            }

            alarmCalendar.set(calDate[0], calDate[1], calDate[2], calDate[3], calDate[4], 0)
            if (alarmCalendar.timeInMillis > System.currentTimeMillis()) {
                setAlarm()
                Log.d("yyjLog", "alarm저장: " + alarmCalendar.time)
            }
            else {
                Toast.makeText(this, "현재 시간 이후로 설정해 주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun alertCalendar() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_datepicker, null)
        val calView = view.calendarView
        calView.minDate = System.currentTimeMillis() - 1000
        calView.setOnDateChangeListener { calendarView, y, m, d ->
            calDate[0] = y
            calDate[1] = m
            calDate[2] = d
            Log.d("yyjLog", "calendar: " + calDate[0] + ", " + calDate[1] + ", " + calDate[2])
            dialog.dismiss()
        }
        builder.setView(view)
        dialog = builder.create()
    }

    private fun setAlarm() {
        val alarmManager =
                this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            intent = Intent(this, AlarmReceiver::class.java).let { intent ->
                intent.putExtra("channelId", channelId)
                intent.putExtra("message", message)
                PendingIntent.getBroadcast(this, System.currentTimeMillis().toInt(), intent, FLAG_UPDATE_CURRENT)
            }
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            intent)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setSendMsgName(name: String) {
        if (user == 0) {
            message.name = name + "님"
        }
        else if (user == 1) {
            message.name = isUserNameKorean(name)
        }
        else {
            message.name = null
        }
    }

    private fun isUserNameKorean(name: String): String {
        val last = name[name.length - 1]
        if (last < 0xAC00.toChar() || last > 0xD7A3.toChar()) {
            return name
        }
        return if ((last - 0xAC00.toChar()) % 28 > 0) {
            name + "아"
        } else {
            name + "야"
        }
    }
}
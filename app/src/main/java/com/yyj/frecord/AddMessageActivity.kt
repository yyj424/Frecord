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
    private val CHANNEL_ID = "msg"
    private lateinit var dialog : AlertDialog
    private lateinit var intent : PendingIntent
    private val alarmCalendar = Calendar.getInstance()
    private var calDate = arrayOf(alarmCalendar.get(Calendar.YEAR), alarmCalendar.get(Calendar.MONTH),
        alarmCalendar.get(Calendar.DAY_OF_MONTH), alarmCalendar.get(Calendar.HOUR), alarmCalendar.get(Calendar.MINUTE))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_message)
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

        btnSaveMessage.setOnClickListener {
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
        var calView = view.calendarView
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
                intent.putExtra("channelId", CHANNEL_ID)
                intent.putExtra("title", "id")
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
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
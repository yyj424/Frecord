package com.yyj.frecord

import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_message.*
import kotlinx.android.synthetic.main.dialog_datepicker.view.*
import java.util.*


class AddMessageActivity : AppCompatActivity() {
    private val CHANNEL_ID = "msg"
    private lateinit var dialog : AlertDialog
    private lateinit var intent : PendingIntent
    private val alarmCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_message)

        setAlarmDate()
        alarmCalendar.set(Calendar.HOUR, timePicker.hour)
        alarmCalendar.set(Calendar.MINUTE, timePicker.minute)
        alarmCalendar.set(Calendar.SECOND, 0)
        createNotificationChannel()

        btnPickDate.setOnClickListener {
            dialog.show()
        }
        var h = 0
        var m = 0
        timePicker.setOnTimeChangedListener { _, hour, minute ->
            alarmCalendar.set(Calendar.HOUR, hour)
            alarmCalendar.set(Calendar.MINUTE, minute)
        }

        btnSaveMessage.setOnClickListener {
            Log.d("yyjLog", "alarm저장: " + alarmCalendar.time)
//            val alarmManager =
//                this.getSystemService(Context.ALAaRM_SERVICE) as AlarmManager
//
//            intent = Intent(this, AlarmReceiver::class.java).let { intent ->
//                intent.putExtra("channelId", CHANNEL_ID)
//                intent.putExtra("title", "id")
//                PendingIntent.getBroadcast(this, System.currentTimeMillis().toInt(), intent, FLAG_UPDATE_CURRENT)
//            }
//
//            var calendar = Calendar.getInstance()
//            calendar.timeInMillis = System.currentTimeMillis()
//            calendar.add(Calendar.SECOND, 10)
//            alarmManager?.set(
//                AlarmManager.RTC_WAKEUP,
//                SystemClock.elapsedRealtime() + 1 * 1000,
//                intent)
        }
    }

    private fun setAlarmDate() {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_datepicker, null)
        var calView = view.calendarView
        calView.minDate = System.currentTimeMillis() - 1000
        calView.setOnDateChangeListener { calendarView, year, month, day ->
            alarmCalendar.set(year, month, day)
            Log.d("yyjLog", "alarmCal: " + alarmCalendar.time)
            dialog.dismiss()
        }
        builder.setView(view)
        dialog = builder.create()
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
package com.yyj.frecord

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_message.*
import java.util.*


class AddMessageActivity : AppCompatActivity() {
    private val CHANNEL_ID = "msg"
    private lateinit var intent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_message)

        createNotificationChannel()

        btnSaveMessage.setOnClickListener {
            val alarmManager =
                this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            intent = Intent(this, AlarmReceiver::class.java).let { intent ->
                intent.putExtra("channelId", CHANNEL_ID)
                intent.putExtra("title", "id")
                PendingIntent.getBroadcast(this, System.currentTimeMillis().toInt(), intent, FLAG_UPDATE_CURRENT)
            }//동작 확인 필요

            var calendar = Calendar.getInstance()
            calendar.timeInMillis = System.currentTimeMillis()
            calendar.add(Calendar.SECOND, 10)
            alarmManager?.set(
                AlarmManager.RTC_WAKEUP,
                SystemClock.elapsedRealtime() + 1 * 1000,
                intent)
        }
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
package com.yyj.frecord

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra("title")
        val id = intent.getStringExtra("channelId")

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }//임시 intent
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        Log.d("yyjLog", "con: " + context + ",,,id: " + id)
        var builder = NotificationCompat.Builder(context, id!!)
            .setSmallIcon(R.drawable.ic_selected_view) // temporal icon
            .setContentTitle("My notification")
            .setContentText("Much longer text that cannot fit one line...")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Much longer text that cannot fit one line..."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        // 긴 줄이면 화면 크게 적용

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(100, builder.build())
            Log.d("yyjLog", "notify")
        }
    }
}
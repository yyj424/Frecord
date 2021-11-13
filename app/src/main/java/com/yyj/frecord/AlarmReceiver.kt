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
        val message = intent.getSerializableExtra("message") as MessageData
        val channelId = intent.getStringExtra("channelId")

        val intent = Intent(context, ViewMessageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("message", message)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, 0)

        var builder = NotificationCompat.Builder(context, channelId!!)
            .setSmallIcon(R.drawable.ic_selected_view) // temporal icon
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(message.name + ", " + message.content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (message.name == null) {
            builder.setContentText(message.content)
        }
        else {
            builder.setContentText(message.name + ", " + message.content)
        }

        with(NotificationManagerCompat.from(context)) {
            notify(100, builder.build())
        }
    }
}
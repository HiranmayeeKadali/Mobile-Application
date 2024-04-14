package com.coddle.receivers

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationChannel

import android.app.NotificationManager
import android.os.Build


class MyNotificationPublisher : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "Coddle"
        const val NOTIFICATION = "Event notification"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification = intent.getParcelableExtra(NOTIFICATION)!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                "Coddle_notifications",
                "Coddle",
                importance
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
        notificationManager.notify(id, notification)
    }
}
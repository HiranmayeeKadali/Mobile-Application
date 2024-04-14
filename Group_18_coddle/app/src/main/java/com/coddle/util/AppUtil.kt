package com.coddle.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.coddle.R
import com.coddle.receivers.MyNotificationPublisher


class AppUtil {
    companion object {
        private var alertDialog: AlertDialog? = null
        private const val secondMillis = 1000
        private const val minuteMillis = 60 * secondMillis
        private const val hourMillis = 60 * minuteMillis
        private const val daysMillis = 24 * hourMillis

        fun Context.showProgress(message: String) {
            if (alertDialog == null)
                alertDialog = getDialog()

            val view = View.inflate(this, R.layout.progress_view, null)
            view.findViewById<TextView>(R.id.txtMessage).text = message

            alertDialog!!.apply {
                if (isShowing)
                    this.cancel()
                setView(view)
            }.show()
        }

        private fun Context.getDialog(): AlertDialog {
            return AlertDialog.Builder(this)
                .create()
        }

        fun dismissProgress() {
            alertDialog?.cancel()
            alertDialog = null
        }

        fun Context.shortToast(message: String) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        @SuppressLint("UnspecifiedImmutableFlag")
        fun Context.scheduleNotification(notification: Notification, delay: Long) {
            val notificationIntent = Intent(this, MyNotificationPublisher::class.java)
            notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1)
            notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val alarmManager = (getSystemService(Context.ALARM_SERVICE) as AlarmManager?)!!
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, delay, 60000, pendingIntent)
        }

        fun Context.getNotification(content: String): Notification {
            val builder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, "default")
            builder.setContentTitle("Coddle Notification")
            builder.setContentText(content)
            builder.setSmallIcon(R.drawable.ic_launcher_foreground)
            builder.setAutoCancel(true)
            builder.setChannelId("Coddle_notifications")
            return builder.build()
        }

        fun getTimeAgo(timeStamp: Long): CharSequence? {
            var time = timeStamp
            if (time < 1000000000000L) {
                time *= 1000
            }

            val now = System.currentTimeMillis()
            if (time > now || time <= 0) {
                return null
            }


            val diff: Long = now - time
            return when {
                diff < minuteMillis -> {
                    "just now"
                }
                diff < 2 * minuteMillis -> {
                    "1 min ago"
                }
                diff < 50 * minuteMillis -> {
                    " ${diff / minuteMillis} min ago"
                }
                diff < 90 * minuteMillis -> {
                    "1 hr ago"
                }
                diff < 24 * hourMillis -> {
                    " ${diff / hourMillis} hr ago"
                }
                diff < 48 * hourMillis -> {
                    "yesterday"
                }
                else -> {
                    " ${diff / daysMillis} d ago"
                }
            }
        }


    }
}
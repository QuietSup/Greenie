package com.example.greenie.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.greenie.R

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        val hour = intent.getStringExtra("EXTRA_HOUR") ?: return
        val minute = intent.getStringExtra("EXTRA_MINUTE") ?: return
        val channelId = intent.getStringExtra("CHANNEL_ID") ?: return
        val textTitle = intent.getStringExtra("TITLE") ?: return
        val interval = intent.getStringExtra("INTERVAL") ?: return
        val alarmItem = AlarmItem(hours = hour.toInt(), minutes = minute.toInt(), message = message, channelId = channelId.toInt(), textTitle = textTitle, interval = interval.toInt())
        println("Alarm triggered: $message")
        notify(context, message, alarmItem)
    }

    private fun notify(context: Context, textContent: String, alarmItem: AlarmItem) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager, alarmItem.channelId.toString())

        val builder = NotificationCompat.Builder(context, alarmItem.channelId.toString())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(alarmItem.textTitle)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify((0..1000).random(), builder)
        AlarmScheduler(context).schedule(alarmItem)
    }

    private fun createNotificationChannel(notificationManager: NotificationManager, channelId: String) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        val name = "reminders"
        val descriptionText = "Reminders about plants treatment"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
        println("Channel created: ${notificationManager.notificationChannels}")
    }
}
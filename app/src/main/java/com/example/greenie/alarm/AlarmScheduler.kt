package com.example.greenie.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import java.util.concurrent.TimeUnit

class AlarmScheduler(
    private val context: Context,
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

//    fun schedule(item: AlarmItem) {
//        val intent = Intent(context, AlarmReceiver::class.java).apply {
//            putExtra("EXTRA_MESSAGE", item.message)
//            putExtra("EXTRA_HOUR", item.hours.toString())
//            putExtra("EXTRA_MINUTE", item.minutes.toString())
//            putExtra("CHANNEL_ID", item.channelId)
//            putExtra("TITLE", item.textTitle)
//        }
//
//        val calendar: Calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            set(Calendar.HOUR_OF_DAY, item.hours)
//            set(Calendar.MINUTE, item.minutes)
//            set(Calendar.SECOND, 0)
//        }
//        var alarmDateTime = calendar.timeInMillis
//        if (calendar.timeInMillis < System.currentTimeMillis()) {
//            alarmDateTime = calendar.timeInMillis + TimeUnit.DAYS.toMillis(1)
//        }
//
//        alarmManager.setExact(
//            AlarmManager.RTC,
//            alarmDateTime,
//            PendingIntent.getBroadcast(
//                context,
//                item.hashCode(),
//                intent,
//                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//            )
//        )
//
//
//        println("Repeating alarm is set on ${item.hours}:${item.minutes}")
//    }

    fun schedule(item: AlarmItem) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("EXTRA_MESSAGE", item.message)
            putExtra("EXTRA_HOUR", item.hours.toString())
            putExtra("EXTRA_MINUTE", item.minutes.toString())
            putExtra("CHANNEL_ID", item.channelId.toString())
            putExtra("TITLE", item.textTitle)
            putExtra("INTERVAL", item.interval.toString())
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, item.hours)
            set(Calendar.MINUTE, item.minutes)
            set(Calendar.SECOND, 0)
        }
        var alarmDateTime = calendar.timeInMillis
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            alarmDateTime = calendar.timeInMillis + TimeUnit.DAYS.toMillis(item.interval.toLong())
        }

        alarmManager.setExact(
            AlarmManager.RTC,
            alarmDateTime,
            PendingIntent.getBroadcast(
                context,
                item.channelId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
        println("Repeating alarm is set on ${item.hours}:${item.minutes} with interval ${item.interval}")
    }

    fun cancel(channelId: Int) {
        println("alarm $channelId is cancelled")
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                channelId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }


    fun alarmExists(channelId: Int): PendingIntent? {
        return (
            PendingIntent.getBroadcast(
                context,
                channelId,
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }
}
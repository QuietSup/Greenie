package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase
import com.example.greenie.data.models.Alarm

class AlarmRepository(private val mainDatabase: MainDatabase) {
    fun getAllAlarms() = mainDatabase.alarmDao().getAllAlarms()

//    fun getAlarmById(id: Int) = mainDatabase.alarmDao().getAlarmById(id)

    fun getAlarmByPlantId(plantId: Int) = mainDatabase.alarmDao().getAlarmByPlantId(plantId)

    suspend fun insertAlarm(alarm: Alarm) = mainDatabase.alarmDao().insertAlarm(alarm)

    suspend fun upsertAlarm(alarm: Alarm) = mainDatabase.alarmDao().upsertAlarm(alarm)

    suspend fun updateAlarm(alarm: Alarm) = mainDatabase.alarmDao().updateAlarm(alarm)
}
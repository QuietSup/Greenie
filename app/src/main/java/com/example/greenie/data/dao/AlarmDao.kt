package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.greenie.data.models.Alarm
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {
    @Query("SELECT * FROM alarms")
    fun getAllAlarms(): Flow<List<Alarm>>

//    @Query("SELECT * FROM alarms WHERE id = :id LIMIT 1")
//    fun getAlarmById(id: Int): Flow<Alarm>

    @Query("SELECT * FROM alarms WHERE plant_id = :plantId")
    fun getAlarmByPlantId(plantId: Int): Flow<Alarm?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlarm(alarm: Alarm): Long

    @Upsert
    suspend fun upsertAlarm(alarm: Alarm): Long

    @Update(onConflict = OnConflictStrategy.IGNORE)
    suspend fun updateAlarm(alarm: Alarm): Int
}
package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.greenie.data.models.Plant
import com.example.greenie.data.models.TextRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface TextRecordDao {
    @Query("SELECT * FROM text_record")
    fun getAllTextRecords(): Flow<List<TextRecord>>

    @Query("SELECT * FROM text_record WHERE plant_id = :plantId")
    fun getTextRecordsByPlantId(plantId: Int): Flow<List<TextRecord>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTextRecord(textRecord: TextRecord): Long

    @Delete
    suspend fun deleteTextRecord(vararg users: TextRecord): Int
}
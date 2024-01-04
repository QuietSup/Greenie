package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.example.greenie.data.models.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :id LIMIT 1")
    fun getPlantById(id: Int): Flow<Plant>

    @Query("SELECT * FROM plants WHERE modified_at IS NOT NULL ORDER BY modified_at DESC")
    fun getPlantsSortedByModifiedAt(): Flow<List<Plant>>

    @Update
    suspend fun updatePlant(plant: Plant): Int
}
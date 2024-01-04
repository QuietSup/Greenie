package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.greenie.data.models.Plant
import com.example.greenie.data.models.Treatment
import kotlinx.coroutines.flow.Flow

@Dao
interface TreatmentDao {
    @Query("SELECT * FROM treatments")
    fun getAllTreatments(): Flow<List<Treatment>>

    @Query("SELECT * FROM treatments WHERE disease_id = :diseaseId")
    fun getTreatmentsByDiseaseId(diseaseId: Int): Flow<List<Treatment>>
}
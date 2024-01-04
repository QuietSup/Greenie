package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.greenie.data.models.Diagnosis
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Query("SELECT * FROM diagnoses")
    fun getAllDiagnoses(): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE id = :id LIMIT 1")
    fun getDiagnosisById(id: Long): Flow<Diagnosis>

    @Query("SELECT * FROM diagnoses WHERE disease_id IN (SELECT id FROM diseases WHERE plant_id = :plantId)")
    fun getDiagnosesByPlantId(plantId: Int): Flow<List<Diagnosis>>

    @Query("SELECT * FROM diagnoses WHERE disease_id IN (SELECT id FROM diseases WHERE plant_id = :plantId) ORDER BY created_at DESC LIMIT 1")
    fun getLastDiagnosisByPlantId(plantId: Int): Flow<Diagnosis>

    @Insert
    suspend fun insertDiagnosis(diagnosis: Diagnosis): Long

    @Delete
    suspend fun deleteDiagnosis(diagnosis: Diagnosis): Int
}
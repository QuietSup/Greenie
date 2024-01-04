package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.greenie.data.models.Disease
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseaseDao {
    @Query("SELECT * FROM diseases")
    fun getAllDiseases() : Flow<List<Disease>>

    @Query("SELECT * FROM diseases WHERE id = :id LIMIT 1")
    fun getDiseaseById(id: Int) : Flow<Disease>

    @Query("SELECT * FROM diseases WHERE tflite_code = :code LIMIT 1")
    fun getDiseaseByTfcode(code: Int) : Flow<Disease>
}
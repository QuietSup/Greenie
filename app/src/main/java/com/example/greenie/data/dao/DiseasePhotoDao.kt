package com.example.greenie.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.greenie.data.models.DiseasePhoto
import com.example.greenie.data.models.Plant
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseasePhotoDao {
    @Query("SELECT * FROM disease_photos")
    fun getAllDiseasePhotos(): Flow<List<DiseasePhoto>>

//    @Query("SELECT * FROM disease_photos WHERE ")
//    fun getDiseasePhotoBy(): Flow<DiseasePhoto>
}
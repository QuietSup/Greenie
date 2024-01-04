package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase

class DiseasePhotoRepository(private val mainDatabase: MainDatabase) {
    fun getAllDiseasePhotos() = mainDatabase.diseasePhotoDao().getAllDiseasePhotos()
}
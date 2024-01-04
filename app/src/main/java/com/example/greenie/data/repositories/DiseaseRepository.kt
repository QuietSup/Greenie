package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase

class DiseaseRepository(private val mainDatabase: MainDatabase) {
    fun getAllDiseases() = mainDatabase.diseaseDao().getAllDiseases()

    fun getDiseaseById(id: Int) = mainDatabase.diseaseDao().getDiseaseById(id)

    fun getDiseaseByTfcode(code: Int) = mainDatabase.diseaseDao().getDiseaseByTfcode(code)
}
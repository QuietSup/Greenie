package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase

class TreatmentRepository(private val mainDatabase: MainDatabase) {
    fun getAllTreatments() = mainDatabase.treatmentDao().getAllTreatments()

    fun getTreatmentsByDiseaseId(diseaseId: Int) = mainDatabase.treatmentDao().getTreatmentsByDiseaseId(diseaseId)
}
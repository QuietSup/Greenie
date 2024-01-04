package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase
import com.example.greenie.data.models.Diagnosis

class DiagnosisRepository(private val mainDatabase: MainDatabase) {
    fun getAllDiagnoses() = mainDatabase.diagnosisDao().getAllDiagnoses()

    fun getDiagnosesByPlantId(plantId: Int) = mainDatabase.diagnosisDao().getDiagnosesByPlantId(plantId)

    fun getDiagnosisById(id: Long) = mainDatabase.diagnosisDao().getDiagnosisById(id)

    fun getLastDiagnosisByPlantId(plantId: Int) = mainDatabase.diagnosisDao().getLastDiagnosisByPlantId(plantId)

    suspend fun insertDiagnosis(diagnosis: Diagnosis) = mainDatabase.diagnosisDao().insertDiagnosis(diagnosis)

    suspend fun deleteDiagnosis(diagnosis: Diagnosis) = mainDatabase.diagnosisDao().deleteDiagnosis(diagnosis)
}
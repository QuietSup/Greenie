package com.example.greenie.data.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.greenie.data.repositories.AlarmRepository
import com.example.greenie.data.repositories.DiagnosisRepository
import com.example.greenie.data.repositories.DiseasePhotoRepository
import com.example.greenie.data.repositories.DiseaseRepository
import com.example.greenie.data.repositories.PlantRepository
import com.example.greenie.data.repositories.TextRecordRepository
import com.example.greenie.data.repositories.TreatmentRepository

class ViewModelFactory(
    private val alarmRepository: AlarmRepository,
    private val diagnosisRepository: DiagnosisRepository,
    private val diseaseRepository: DiseaseRepository,
    private val diseasePhotoRepository: DiseasePhotoRepository,
    private val plantRepository: PlantRepository,
    private val textRecordRepository: TextRecordRepository,
    private val treatmentRepository: TreatmentRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
//        return super.create(modelClass)
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(
                alarmRepository,
                diagnosisRepository,
                diseaseRepository,
                diseasePhotoRepository,
                plantRepository,
                textRecordRepository,
                treatmentRepository,
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
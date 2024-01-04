package com.example.greenie.data.viewmodels

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.greenie.data.models.Alarm
import com.example.greenie.data.models.Diagnosis
import com.example.greenie.data.models.Plant
import com.example.greenie.data.models.TextRecord
import com.example.greenie.data.repositories.AlarmRepository
import com.example.greenie.data.repositories.DiagnosisRepository
import com.example.greenie.data.repositories.DiseasePhotoRepository
import com.example.greenie.data.repositories.DiseaseRepository
import com.example.greenie.data.repositories.PlantRepository
import com.example.greenie.data.repositories.TextRecordRepository
import com.example.greenie.data.repositories.TreatmentRepository
import kotlinx.coroutines.launch
import java.io.FileOutputStream

class MainViewModel(
    private val alarmRepository: AlarmRepository,
    private val diagnosisRepository: DiagnosisRepository,
    private val diseaseRepository: DiseaseRepository,
    private val diseasePhotoRepository: DiseasePhotoRepository,
    private val plantRepository: PlantRepository,
    private val textRecordRepository: TextRecordRepository,
    private val treatmentRepository: TreatmentRepository,
): ViewModel() {

//    -------------------------------------------------------------------------
//    Alarm
    fun getAllAlarms() = alarmRepository.getAllAlarms().asLiveData(viewModelScope.coroutineContext)

//    fun getAlarmById(id: Int) = alarmRepository.getAlarmById(id).asLiveData(viewModelScope.coroutineContext)

    fun getAlarmByPlantId(plantId: Int) = alarmRepository.getAlarmByPlantId(plantId).asLiveData(viewModelScope.coroutineContext)

//    fun insertAlarm(hours: Int, minutes: Int, plantId: Int, title: String, tooltip: String, interval: ) = viewModelScope.launch {
//        val alarm = Alarm(
//            hours = hours,
//            minutes = minutes,
//            plantId = plantId,
//            title = title,
//            tooltip = tooltip,
//            interval = interval
//        )
//        val id = alarmRepository.insertAlarm(alarm)
//        println(id)
//    }
    fun upsertAlarm(hours: Int, minutes: Int, plantId: Int, title: String, tooltip: String, interval: Int) = viewModelScope.launch {
        val alarm = Alarm(
            hours = hours,
            minutes = minutes,
            plantId = plantId,
            title = title,
            tooltip = tooltip,
            interval = interval
        )
        val id = alarmRepository.upsertAlarm(alarm)
        println(id)
    }

    fun updateAlarm(alarm: Alarm) = viewModelScope.launch {
          alarmRepository.updateAlarm(alarm)
    }

//    -------------------------------------------------------------------------
//    Diagnosis
    fun getAllDiagnoses() = diagnosisRepository.getAllDiagnoses().asLiveData(viewModelScope.coroutineContext)

    fun getDiagnosisById(id: Long) = diagnosisRepository.getDiagnosisById(id).asLiveData(viewModelScope.coroutineContext)

    fun getDiagnosesByPlantId(plantId: Int) = diagnosisRepository.getDiagnosesByPlantId(plantId).asLiveData(viewModelScope.coroutineContext)

    fun getLastDiagnosisByPlantId(plantId: Int) = diagnosisRepository.getLastDiagnosisByPlantId(plantId).asLiveData(viewModelScope.coroutineContext)

    fun insertDiagnosis(
        diseaseId: Int,
        photo: Bitmap,
        context: Context,
        setDiagnosisId: (Long?) -> Unit
    ) = viewModelScope.launch {
        val diagnosis = Diagnosis(
            createdAt = System.currentTimeMillis(),
            diseaseId = diseaseId,
            tooltip = null
        )
        val id = diagnosisRepository.insertDiagnosis(diagnosis)
        println(id)
        writeFileToInternalStorage(context, photo, "$id.png")
        setDiagnosisId(id)
    }

    private fun writeFileToInternalStorage(context: Context, outputImage: Bitmap, fileName: String = "myFile.png"): String {
        val fos: FileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE)
        outputImage.compress(Bitmap.CompressFormat.PNG, 90, fos)
        val cacheDir = context.filesDir
        println("create")
        return cacheDir.path + fileName
    }

    fun deleteDiagnosis(diagnosis: Diagnosis, context: Context) = viewModelScope.launch {
        if (deleteFileFromInternalStorage(context, "${diagnosis.id}.png")) {
            println("File deleted")
        } else {
            println("File wasn't deleted")
        }
        diagnosisRepository.deleteDiagnosis(diagnosis)
    }

    private fun deleteFileFromInternalStorage(context: Context, fileName: String = "myFile.png"): Boolean {
        return context.deleteFile(fileName)
    }


//    -------------------------------------------------------------------------
//    Disease
    fun getAllDiseases() = diseaseRepository.getAllDiseases().asLiveData(viewModelScope.coroutineContext)

    fun getDiseaseByTfcode(code: Int) = diseaseRepository.getDiseaseByTfcode(code).asLiveData(viewModelScope.coroutineContext)

    fun getDiseaseById(id: Int) = diseaseRepository.getDiseaseById(id).asLiveData(viewModelScope.coroutineContext)


//    -------------------------------------------------------------------------
//    DiseasePhoto
    fun getAllDiseasePhotos() = diseasePhotoRepository.getAllDiseasePhotos().asLiveData(viewModelScope.coroutineContext)


//    -------------------------------------------------------------------------
//    Plant
    fun getAllPlants() = plantRepository.getAllPlants().asLiveData(viewModelScope.coroutineContext)

    fun getPlantById(id: Int) = plantRepository.getPlantById(id).asLiveData(viewModelScope.coroutineContext)

    fun getPlantsSortedByModifiedAt() = plantRepository.getPlantsSortedByModifiedAt().asLiveData(viewModelScope.coroutineContext)

    fun updatePlant(id: Int, name: String, photoFolder: String?) = viewModelScope.launch {
        val plant = Plant(
            id = id,
            name = name,
            modifiedAt = System.currentTimeMillis(),
            photoFolder = photoFolder
        )
        plantRepository.updatePlant(plant)
    }

//    -------------------------------------------------------------------------
//    TextRecord
    fun getAllTextRecords() = textRecordRepository.getAllTextRecords().asLiveData(viewModelScope.coroutineContext)

    fun getTextRecordsByPlantId(plantId: Int) = textRecordRepository.getTextRecordsByPlantId(plantId).asLiveData(viewModelScope.coroutineContext)

    fun insertTextRecord(text: String, plantId: Int) = viewModelScope.launch {
        val textRecord = TextRecord(
            plantId=plantId,
            createdAt = System.currentTimeMillis(),
            text = text,
        )
        val a = textRecordRepository.insertTextRecord(textRecord)
        println(a)
    }

    fun deleteTextRecord(textRecord: TextRecord) = viewModelScope.launch {
        textRecordRepository.deleteTextRecord(textRecord)
    }

//    -------------------------------------------------------------------------
//    Treatment
    fun getAllTreatments() = treatmentRepository.getAllTreatments().asLiveData(viewModelScope.coroutineContext)

    fun getTreatmentsByDiseaseId(diseaseId: Int) = treatmentRepository.getTreatmentsByDiseaseId(diseaseId).asLiveData(viewModelScope.coroutineContext)
}
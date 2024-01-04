package com.example.greenie.data.repositories

import com.example.greenie.data.MainDatabase
import com.example.greenie.data.models.TextRecord

class TextRecordRepository(private val mainDatabase: MainDatabase) {
    fun getAllTextRecords() = mainDatabase.textRecordDao().getAllTextRecords()

    fun getTextRecordsByPlantId(plantId: Int) = mainDatabase.textRecordDao().getTextRecordsByPlantId(plantId)

    suspend fun insertTextRecord(textRecord: TextRecord) = mainDatabase.textRecordDao().insertTextRecord(textRecord)

    suspend fun deleteTextRecord(textRecord: TextRecord) = mainDatabase.textRecordDao().deleteTextRecord(textRecord)
}
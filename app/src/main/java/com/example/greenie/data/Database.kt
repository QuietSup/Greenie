package com.example.greenie.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.greenie.data.dao.AlarmDao
import com.example.greenie.data.dao.DiagnosisDao
import com.example.greenie.data.dao.DiseaseDao
import com.example.greenie.data.dao.DiseasePhotoDao
import com.example.greenie.data.dao.PlantDao
import com.example.greenie.data.dao.TextRecordDao
import com.example.greenie.data.dao.TreatmentDao
import com.example.greenie.data.models.Alarm
import com.example.greenie.data.models.Diagnosis
import com.example.greenie.data.models.Disease
import com.example.greenie.data.models.DiseasePhoto
import com.example.greenie.data.models.Plant
import com.example.greenie.data.models.TextRecord
import com.example.greenie.data.models.Treatment

@Database(
    version = 12,
    exportSchema = false,
    entities = [
        Diagnosis::class,
        Disease::class,
        DiseasePhoto::class,
        Plant::class,
        TextRecord::class,
        Treatment::class,
        Alarm::class,
    ],
)
abstract class MainDatabase: RoomDatabase() {
    abstract fun alarmDao() : AlarmDao
    abstract fun diagnosisDao() : DiagnosisDao
    abstract fun diseaseDao() : DiseaseDao
    abstract fun diseasePhotoDao() : DiseasePhotoDao
    abstract fun plantDao() : PlantDao
    abstract fun textRecordDao() : TextRecordDao
    abstract fun treatmentDao() : TreatmentDao

    companion object {
        private fun buildDatabase(context: Context) : MainDatabase {
            return Room.databaseBuilder(context, MainDatabase::class.java, "main.db")
                .createFromAsset("sample11.sqlite")
//                .fallbackToDestructiveMigration()
                .build()
        }

        @Volatile
        private var INSTANCE : MainDatabase? = null

        fun getDatabase(context: Context): MainDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    INSTANCE = buildDatabase(context)
                }
            }
            return INSTANCE!!
        }
    }
}
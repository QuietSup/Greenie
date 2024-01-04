package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diseases",
    foreignKeys = [ForeignKey(entity = Plant::class, parentColumns = ["id"], childColumns = ["plant_id"])],
    indices = [Index(value = ["tflite_code"],
        unique = true)]
)
data class Disease(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "reminder_interval")
    val reminderInterval: Int?,

    @ColumnInfo(name = "reminder_tooltip")
    val reminderTooltip: String?,

    @ColumnInfo(name = "photo_folder")
    val photoFolder: String?,

    @ColumnInfo(name = "plant_id")
    val plantId: Int,

    @ColumnInfo(name = "tflite_code")
    val tfliteCode: Int,
)

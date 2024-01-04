package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "disease_photos",
    foreignKeys = [ForeignKey(entity = Disease::class, parentColumns = ["id"], childColumns = ["disease_id"])],
)
data class DiseasePhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "disease_id")
    val diseaseId: Int,

    @ColumnInfo(name = "tooltip")
    val tooltip: String?,
)


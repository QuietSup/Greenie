package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "treatments",
    foreignKeys = [ForeignKey(entity = Disease::class, parentColumns = ["id"], childColumns = ["disease_id"])],
)
data class Treatment(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "disease_id")
    val diseaseId: Int,

    @ColumnInfo(name = "text")
    val text: String,
)


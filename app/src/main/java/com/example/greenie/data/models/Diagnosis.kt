package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "diagnoses",
    foreignKeys = [ForeignKey(entity = Disease::class, parentColumns = ["id"], childColumns = ["disease_id"])]
)
data class Diagnosis(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "disease_id")
    val diseaseId: Int,

    val tooltip: String?,

    @ColumnInfo(name = "created_at")
    override val createdAt: Long,
): Record

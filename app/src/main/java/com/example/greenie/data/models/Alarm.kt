package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "alarms",
    foreignKeys = [ForeignKey(entity = Plant::class, parentColumns = ["id"], childColumns = ["plant_id"])],
)
data class Alarm(
    @ColumnInfo(name = "hours")
    val hours: Int,

    @ColumnInfo(name = "minutes")
    val minutes: Int,

    @PrimaryKey
    @ColumnInfo(name = "plant_id")
    val plantId: Int,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "tooltip")
    val tooltip: String,

    @ColumnInfo(name = "is_active")
    val isActive: Int = 1,

    @ColumnInfo(name = "interval")
    val interval: Int,
    )

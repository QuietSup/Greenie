package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "text_record",
    foreignKeys = [ForeignKey(entity = Plant::class, parentColumns = ["id"], childColumns = ["plant_id"])],
    )
data class TextRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "plant_id")
    val plantId: Int,

    @ColumnInfo(name = "created_at")
    override val createdAt: Long,

    @ColumnInfo(name = "text")
    val text: String,
): Record


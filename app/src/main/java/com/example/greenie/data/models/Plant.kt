package com.example.greenie.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "plants",
    indices = [Index(value = ["name"],
        unique = true)]
)
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "photo_folder")
    val photoFolder: String?,

    @ColumnInfo(name = "modified_at")
    val modifiedAt: Long?,
)

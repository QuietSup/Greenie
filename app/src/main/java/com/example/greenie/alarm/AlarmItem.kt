package com.example.greenie.alarm

data class AlarmItem(
    val hours: Int,
    val minutes: Int,
    val message: String,
    val channelId: Int,
    val textTitle: String,
    val interval: Int, // days
)
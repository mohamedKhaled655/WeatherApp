package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String,
    val startTime: Long,
    val endTime: Long,
    val isNotification: Boolean,
    val isActive: Boolean
)

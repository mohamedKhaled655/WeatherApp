package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_alerts")
data class AlertEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val alertType: String,
    val notificationType: String,
    val isSound: Boolean,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val latitude: Double,
    val longitude: Double
)

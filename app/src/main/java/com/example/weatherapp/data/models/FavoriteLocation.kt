package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val country:String,
    val latitude: Double,
    val longitude: Double
)

package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather")
data class CurrentWeatherModel(
    @PrimaryKey
    val id: Int,

    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val dt: Long,
    val sys: Sys,
    val timezone: Int,
    val name: String,
    val cod: Int
)

data class Coord(val lon: Double, val lat: Double)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int? = null,
    val grnd_level: Int? = null
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double? = null
)

data class Clouds(val all: Int)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

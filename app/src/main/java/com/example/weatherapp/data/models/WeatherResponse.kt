package com.example.weatherapp.data.models

data class WeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherModel>,
    val city: City
)
package com.example.weatherapp.data.models

data class ForecastResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherForecast>,
    val city: City
)
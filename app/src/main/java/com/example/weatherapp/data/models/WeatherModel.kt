package com.example.weatherapp.data.models



data class WeatherModel(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val sys: ForecastSys,
    val dt_txt: String
)


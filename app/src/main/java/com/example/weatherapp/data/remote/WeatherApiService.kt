package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.ForecastResponse
import com.example.weatherapp.data.models.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") language: String,
        @Query("appid") apiKey: String="9ea5f900ad4ca28e2ff79a89a0a7ff75"
    ): CurrentWeatherModel


    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") language: String,
        @Query("appid") apiKey: String="9ea5f900ad4ca28e2ff79a89a0a7ff75"
    ): WeatherResponse
}
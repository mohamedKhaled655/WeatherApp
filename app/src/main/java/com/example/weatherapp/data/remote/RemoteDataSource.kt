package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import org.intellij.lang.annotations.Language
import retrofit2.Response

interface RemoteDataSource {
    suspend fun getCurrentWeather(lat: Double, long: Double,language:String,apiKey:String): CurrentWeatherModel
    suspend fun getForecast(lat: Double, long: Double,language:String,apiKey:String): Flow<WeatherResponse>
}
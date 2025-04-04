package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource ( private val currentWeather: CurrentWeatherModel ):RemoteDataSource{
    override suspend fun getCurrentWeather(
        lat: Double,
        long: Double,
        language: String,
        apiKey: String
    ): Flow<CurrentWeatherModel> {
        return flow {emit(currentWeather)}
    }

    override suspend fun getForecast(
        lat: Double,
        long: Double,
        language: String,
        apiKey: String
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }
}
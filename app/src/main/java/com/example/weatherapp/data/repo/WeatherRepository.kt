package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, long: Double): CurrentWeatherModel
    suspend fun getForecast(lat: Double, long: Double): Flow<WeatherResponse>

///database
    suspend fun getAllFavLocations():Flow<List<FavoriteLocationEntity>?>
    suspend fun addFavLocation(favLocation: FavoriteLocationEntity):Long
    suspend fun removeFavLocation(favLocation: FavoriteLocationEntity):Int
}
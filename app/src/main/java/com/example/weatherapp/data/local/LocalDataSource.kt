package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getAllFavLocations():Flow<List<FavoriteLocationEntity>>
    suspend fun insertFavLocation(favLocation: FavoriteLocationEntity):Long
    suspend fun deleteFavLocation(favLocation: FavoriteLocationEntity?):Int
}
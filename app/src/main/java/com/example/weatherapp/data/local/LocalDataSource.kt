package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    suspend fun getAllFavLocations():Flow<List<FavoriteLocationEntity>>
    suspend fun insertFavLocation(favLocation: FavoriteLocationEntity):Long
    suspend fun deleteFavLocation(favLocation: FavoriteLocationEntity?):Int


    ///alert
    suspend fun getAllAlert():Flow<List<AlertEntity>>
    suspend fun getActiveAlerts():Flow<List<AlertEntity>>
    suspend fun getDeactivateAlert(alertEntity: AlertEntity)
    suspend fun insertAlert(alertEntity: AlertEntity):Long
    suspend fun deleteAlert(alertEntity: AlertEntity?):Int
}
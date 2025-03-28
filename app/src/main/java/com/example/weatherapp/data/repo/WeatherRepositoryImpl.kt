package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : WeatherRepository{

    companion object{
        private var INSTANCE:WeatherRepositoryImpl?=null
        fun getInstance(remoteDataSource: RemoteDataSource,
                        localDataSource: LocalDataSource
        ):WeatherRepositoryImpl{
            return INSTANCE?: synchronized(this){
                val temp=WeatherRepositoryImpl(remoteDataSource,
                    localDataSource
                )
                INSTANCE=temp
                temp
            }
        }
    }

    override suspend fun getCurrentWeather(lat: Double, long: Double): CurrentWeatherModel {
        return remoteDataSource.getCurrentWeather(
            lat,
            long,
            "en",
            "9ea5f900ad4ca28e2ff79a89a0a7ff75")
    }

    override suspend fun getForecast(lat: Double, long: Double): Flow<WeatherResponse> {
       return remoteDataSource.getForecast(lat,
           long,
           "en",
           "9ea5f900ad4ca28e2ff79a89a0a7ff75")
    }

    override suspend fun getAllFavLocations(): Flow<List<FavoriteLocationEntity>?> {
        return localDataSource.getAllFavLocations()
    }

    override suspend fun addFavLocation(favLocation: FavoriteLocationEntity): Long {
        return localDataSource.insertFavLocation(favLocation)
    }

    override suspend fun removeFavLocation(favLocation: FavoriteLocationEntity): Int {
        return localDataSource.deleteFavLocation(favLocation)
    }

    override suspend fun getAllAlert(): Flow<List<AlertEntity>> {
        return localDataSource.getAllAlert()
    }

    override suspend fun getActiveAlerts(): Flow<List<AlertEntity>> {
        return localDataSource.getActiveAlerts()
    }

    override suspend fun getDeactivateAlert(alertEntity: AlertEntity) {
        return localDataSource.getDeactivateAlert(alertEntity)
    }

    override suspend fun insertAlert(alertEntity: AlertEntity): Long {
        return localDataSource.insertAlert(alertEntity)
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity?): Int {
        return localDataSource.deleteAlert(alertEntity)
    }


}
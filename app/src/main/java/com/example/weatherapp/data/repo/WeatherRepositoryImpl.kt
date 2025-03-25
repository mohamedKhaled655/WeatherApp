package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow

class WeatherRepositoryImpl private constructor(
    private val remoteDataSource: RemoteDataSource,
   // private val localDataSource: LocalDataSource
) : WeatherRepository{

    companion object{
        private var INSTANCE:WeatherRepositoryImpl?=null
        fun getInstance(remoteDataSource: RemoteDataSource,
                       // localDataSource: LocalDataSource
        ):WeatherRepositoryImpl{
            return INSTANCE?: synchronized(this){
                val temp=WeatherRepositoryImpl(remoteDataSource,
                    //localDataSource
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


}
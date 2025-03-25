package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.CurrentWeatherModel
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource (private val dao: WeatherDao) : LocalDataSource {

}
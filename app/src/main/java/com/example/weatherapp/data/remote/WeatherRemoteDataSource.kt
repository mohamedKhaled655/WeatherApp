package com.example.weatherapp.data.remote

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class WeatherRemoteDataSource(private val service: WeatherApiService) : RemoteDataSource {


    override suspend fun getCurrentWeather(
        lat: Double,
        long: Double,
        language: String,
        apiKey: String
    ): CurrentWeatherModel {
        return service.getCurrentWeather(lat,long,language,apiKey)
    }

    override suspend fun getForecast(
        lat: Double,
        long: Double,
        language: String,
        apiKey: String
    ): Flow<WeatherResponse> {
        return flow {
            emit(service.getWeatherForecast(lat, long, language, apiKey))
        }.catch { e ->
            e.printStackTrace()

        }
    }
}
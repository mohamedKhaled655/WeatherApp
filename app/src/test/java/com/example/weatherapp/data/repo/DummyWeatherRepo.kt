package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocationType
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

class DummyWeatherRepo:WeatherRepository {
    override suspend fun getCurrentWeather(lat: Double, long: Double): Flow<CurrentWeatherModel> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecast(lat: Double, long: Double): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllFavLocations(): Flow<List<FavoriteLocationEntity>?> {
        TODO("Not yet implemented")
    }

    override suspend fun addFavLocation(favLocation: FavoriteLocationEntity): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeFavLocation(favLocation: FavoriteLocationEntity): Int {
        TODO("Not yet implemented")
    }

    override suspend fun getAllWeathers(): Flow<List<CurrentWeatherModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherByLatLng(lat: Double, lon: Double): CurrentWeatherModel? {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(weatherModel: CurrentWeatherModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(weatherModel: CurrentWeatherModel?): Int {
        TODO("Not yet implemented")
    }

    override fun getLocationType(): LocationType {
        TODO("Not yet implemented")
    }

    override fun saveLocationType(locationType: LocationType) {
        TODO("Not yet implemented")
    }

    override fun getTemperatureUnit(): TempUnit {
        TODO("Not yet implemented")
    }

    override fun saveTemperatureUnit(unit: TempUnit) {
        TODO("Not yet implemented")
    }

    override fun getWindSpeedUnit(): WindSpeedUnit {
        TODO("Not yet implemented")
    }

    override fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): Lang {
        TODO("Not yet implemented")
    }

    override fun saveLanguage(language: Lang) {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllAlerts(): Flow<List<WeatherAlert>> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewAlert(alert: WeatherAlert): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeAlert(alertEntity: WeatherAlert?): Int {
        TODO("Not yet implemented")
    }
}
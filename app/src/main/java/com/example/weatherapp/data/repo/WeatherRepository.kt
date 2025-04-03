package com.example.weatherapp.data.repo

import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocationType
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, long: Double): Flow<CurrentWeatherModel>
    suspend fun getForecast(lat: Double, long: Double): Flow<WeatherResponse>

///database
    suspend fun getAllFavLocations():Flow<List<FavoriteLocationEntity>?>
    suspend fun addFavLocation(favLocation: FavoriteLocationEntity):Long
    suspend fun removeFavLocation(favLocation: FavoriteLocationEntity):Int


    ///sharedPref
    fun getLocationType(): LocationType
    fun saveLocationType(locationType: LocationType)

    fun getTemperatureUnit(): TempUnit
    fun saveTemperatureUnit(unit: TempUnit)

    fun getWindSpeedUnit(): WindSpeedUnit
    fun saveWindSpeedUnit(unit: WindSpeedUnit)

    fun getLanguage(): Lang
    fun saveLanguage(language: Lang)


    //alert
   /* suspend fun getAllAlert():Flow<List<AlertEntity>>
    suspend fun getActiveAlerts():Flow<List<AlertEntity>>
    suspend fun getDeactivateAlert(alertEntity: AlertEntity)
    suspend fun insertAlert(alertEntity: AlertEntity):Long
    suspend fun deleteAlert(alertEntity: AlertEntity?):Int
    suspend fun getAlertWithId(alertId: Int):AlertEntity*/


    suspend fun fetchAllAlerts():Flow<List<WeatherAlert>>
    suspend fun addNewAlert(alert: WeatherAlert):Long
    suspend fun removeAlert(alertEntity: WeatherAlert?):Int
}
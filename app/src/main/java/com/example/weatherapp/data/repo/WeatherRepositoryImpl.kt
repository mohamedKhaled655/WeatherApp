package com.example.weatherapp.data.repo

import com.example.weatherapp.data.local.LocalDataSource
import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.remote.RemoteDataSource
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocationType
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
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

    override suspend fun getCurrentWeather(lat: Double, long: Double): Flow<CurrentWeatherModel> {
        return remoteDataSource.getCurrentWeather(
            lat,
            long,
            localDataSource.getLanguage().toString().lowercase(),
            "9ea5f900ad4ca28e2ff79a89a0a7ff75")
    }

    override suspend fun getForecast(lat: Double, long: Double): Flow<WeatherResponse> {
       return remoteDataSource.getForecast(lat,
           long,
           localDataSource.getLanguage().toString().lowercase(),
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

    override suspend fun getAllWeathers(): Flow<List<CurrentWeatherModel>> {
        return localDataSource.getAllWeathers()
    }

    override suspend fun getWeatherByLatLng(lat: Double, lon: Double): CurrentWeatherModel? {
        return localDataSource.getWeatherByLatLng(lat,lon)
    }


    override suspend fun insertWeather(weatherModel: CurrentWeatherModel): Long {
        return localDataSource.insertWeather(weatherModel)
    }

    override suspend fun deleteWeather(weatherModel: CurrentWeatherModel?): Int {
        return localDataSource.deleteWeather(weatherModel)
    }

    override fun getLocationType(): LocationType {
        return localDataSource.getLocationType()
    }

    override fun saveLocationType(locationType: LocationType) {
        localDataSource.saveLocationType(locationType)
    }

    override fun getTemperatureUnit(): TempUnit {
       return localDataSource.getTemperatureUnit()
    }

    override fun saveTemperatureUnit(unit: TempUnit) {
        localDataSource.saveTemperatureUnit(unit)
    }

    override fun getWindSpeedUnit(): WindSpeedUnit {
        return localDataSource.getWindSpeedUnit()
    }

    override fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        localDataSource.saveWindSpeedUnit(unit)
    }

    override fun getLanguage(): Lang {
       return localDataSource.getLanguage()
    }

    override fun saveLanguage(language: Lang) {
       localDataSource.saveLanguage(language)
    }

/*    override suspend fun getAllAlert(): Flow<List<AlertEntity>> {
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

    override suspend fun getAlertWithId(alertId: Int): AlertEntity {
        return localDataSource.getAlertWithId(alertId)
    }*/

    override suspend fun fetchAllAlerts(): Flow<List<WeatherAlert>> {
        return localDataSource.fetchAllAlerts()
    }

    override suspend fun addNewAlert(alert: WeatherAlert): Long {
       return localDataSource.addNewAlert(alert)
    }

    override suspend fun removeAlert(alertEntity: WeatherAlert?): Int {
        return localDataSource.removeAlert(alertEntity)
    }


}
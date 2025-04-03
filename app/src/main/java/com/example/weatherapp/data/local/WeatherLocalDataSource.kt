package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocationType
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource (
    private val dao: WeatherDao,
    //private val alertDao: WeatherAlertDao,
    private val alaram: AlertDao) : LocalDataSource {



    override suspend fun getAllFavLocations(): Flow<List<FavoriteLocationEntity>> {
        return  dao.getFavoritesLocations()
    }

    override suspend fun insertFavLocation(favLocation: FavoriteLocationEntity): Long {
       return dao.addToFavorites(favLocation)
    }

    override suspend fun deleteFavLocation(favLocation: FavoriteLocationEntity?): Int {
        return if(favLocation!=null){
            dao.removeFromFavorites(favLocation)
        }else{
            -1
        }
    }

/*    override suspend fun getAllAlert(): Flow<List<AlertEntity>> {
        return alertDao.getAllAlerts()
    }

    override suspend fun getActiveAlerts(): Flow<List<AlertEntity>> {
        return alertDao.getActiveAlerts()
    }

    override suspend fun getDeactivateAlert(alertEntity: AlertEntity){
        return alertDao.deactivateAlert(alertEntity.id)
    }

    override suspend fun insertAlert(alertEntity: AlertEntity): Long {
        return alertDao.insertAlert(alertEntity)
    }

    override suspend fun deleteAlert(alertEntity: AlertEntity?): Int {
        return if(alertEntity!=null){
            alertDao.removeAlert(alertEntity)
        }else{
            -1
        }
    }

    override suspend fun getAlertWithId(alertId: Int): AlertEntity {

        return alertDao.getAlertWithId(alertId)
    }*/

    override suspend fun fetchAllAlerts(): Flow<List<WeatherAlert>> {
        return alaram.getAllAlerts()
    }

    override suspend fun addNewAlert(alert: WeatherAlert):Long {
        return alaram.insertAlert(alert)
    }

    override suspend fun removeAlert(alertEntity: WeatherAlert?):Int {
        return if(alertEntity!=null){
            alaram.deleteAlert(alertEntity.id)
        }else{
            -1
        }
    }

    override fun getLocationType(): LocationType {
        val value = SharedPreferencesHelper.getString("LOCATION_TYPE", LocationType.GPS.name)
        return LocationType.valueOf(value)
    }

    override fun saveLocationType(locationType: LocationType) {
        SharedPreferencesHelper.putString("LOCATION_TYPE", locationType.name)
    }

    override fun getTemperatureUnit(): TempUnit {
        val value = SharedPreferencesHelper.getString("TEMP_UNIT", TempUnit.Celsius.name)
        return TempUnit.valueOf(value)
    }

    override fun saveTemperatureUnit(unit: TempUnit) {
        SharedPreferencesHelper.putString("TEMP_UNIT",unit.name)
    }

    override fun getWindSpeedUnit(): WindSpeedUnit {
        val value = SharedPreferencesHelper.getString("WIND_UNIT", WindSpeedUnit.MeterPerSec.name)
        return WindSpeedUnit.valueOf(value)
    }

    override fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        SharedPreferencesHelper.putString("WIND_UNIT", unit.name)
    }

    override fun getLanguage(): Lang {
        val value = SharedPreferencesHelper.getString("LANGUAGE", Lang.EN.name)
        return Lang.valueOf(value)
    }

    override fun saveLanguage(language: Lang) {
        SharedPreferencesHelper.putString("LANGUAGE", language.name)
    }


}
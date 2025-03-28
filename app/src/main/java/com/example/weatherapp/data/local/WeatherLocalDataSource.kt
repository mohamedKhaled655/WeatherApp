package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource (private val dao: WeatherDao,private val alertDao: WeatherAlertDao) : LocalDataSource {
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

    override suspend fun getAllAlert(): Flow<List<AlertEntity>> {
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


}
package com.example.weatherapp.data.local

import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource (private val dao: WeatherDao) : LocalDataSource {
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

}
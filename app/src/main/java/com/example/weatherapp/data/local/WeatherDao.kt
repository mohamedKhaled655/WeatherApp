package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToFavorites(favoriteLocationEntity: FavoriteLocationEntity):Long

    @Delete
    suspend fun removeFromFavorites(favoriteLocationEntity: FavoriteLocationEntity):Int

    @Query("SELECT * FROM favorite_locations")
    fun getFavoritesLocations(): Flow<List<FavoriteLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(weatherModel: CurrentWeatherModel):Long

    @Delete
    suspend fun deleteWeather(weatherModel: CurrentWeatherModel):Int

    @Query("SELECT * FROM weather")
    fun getAllWeathers(): Flow<List<CurrentWeatherModel>>

    @Query("SELECT * FROM weather WHERE coord = :coord LIMIT 1")
    fun getWeatherByLatLng(coord: Coord): CurrentWeatherModel?





}
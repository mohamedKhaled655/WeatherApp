package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.CurrentWeatherModel
import kotlinx.coroutines.flow.Flow


@Dao
interface HomeWeatherDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(weatherModel: CurrentWeatherModel):Long

    @Delete
    suspend fun deleteWeather(weatherModel: CurrentWeatherModel):Int

    @Query("SELECT * FROM home_weather")
    fun getAllWeathers(): Flow<List<CurrentWeatherModel>>

    @Query("SELECT * FROM home_weather WHERE coord_lat = :lat AND coord_lon = :lon LIMIT 1")
    fun getWeatherByLatLng(lat: Double, lon: Double): CurrentWeatherModel?

}
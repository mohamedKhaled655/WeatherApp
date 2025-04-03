package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.WeatherAlert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: WeatherAlert):Long

    @Query("SELECT * FROM alerts")
    fun getAllAlerts(): Flow<List<WeatherAlert>>

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteAlert(id: Int):Int
}
package com.example.weatherapp.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherAlertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertEntity):Long

    @Delete
    suspend fun removeAlert(alert: AlertEntity):Int

    @Query("SELECT * FROM weather_alerts WHERE isActive = 1 ORDER BY createdAt DESC")
    fun getActiveAlerts(): Flow<List<AlertEntity>>

    @Query("UPDATE weather_alerts SET isActive = 0 WHERE id = :alertId")
    suspend fun deactivateAlert(alertId: Int)

    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<AlertEntity>>

    @Query("select * from weather_alerts where id = :alertId limit 1")
    fun getAlertWithId(alertId: Int): AlertEntity
}
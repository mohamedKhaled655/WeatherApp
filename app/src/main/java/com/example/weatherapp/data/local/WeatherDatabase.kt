package com.example.weatherapp.data.local

import android.content.Context
import androidx.databinding.adapters.Converters
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity


@Database(entities = [FavoriteLocationEntity::class,AlertEntity::class], version = 3)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun getWeatherDao():WeatherDao
    abstract fun weatherAlertDao():WeatherAlertDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getInstance(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                )
                   // .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}


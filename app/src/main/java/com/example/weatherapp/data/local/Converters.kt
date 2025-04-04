package com.example.weatherapp.data.local



import androidx.room.TypeConverter
import com.example.weatherapp.data.models.Clouds
import com.example.weatherapp.data.models.Coord
import com.example.weatherapp.data.models.Weather
import com.example.weatherapp.data.models.Main
import com.example.weatherapp.data.models.Sys
import com.example.weatherapp.data.models.Wind
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converter {

    @TypeConverter
    fun fromCoord(coord: Coord): String {
        return Gson().toJson(coord)
    }

    @TypeConverter
    fun toCoord(coordString: String): Coord {
        val type = object : TypeToken<Coord>() {}.type
        return Gson().fromJson(coordString, type)
    }

    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherListString: String): List<Weather> {
        val type = object : TypeToken<List<Weather>>() {}.type
        return Gson().fromJson(weatherListString, type)
    }

    @TypeConverter
    fun fromMain(main: Main): String {
        return Gson().toJson(main)
    }

    @TypeConverter
    fun toMain(mainString: String): Main {
        val type = object : TypeToken<Main>() {}.type
        return Gson().fromJson(mainString, type)
    }

    @TypeConverter
    fun fromWind(wind: Wind): String {
        return Gson().toJson(wind)
    }

    @TypeConverter
    fun toWind(windString: String): Wind {
        val type = object : TypeToken<Wind>() {}.type
        return Gson().fromJson(windString, type)
    }

    @TypeConverter
    fun fromClouds(clouds: Clouds): String {
        return Gson().toJson(clouds)
    }

    @TypeConverter
    fun toClouds(cloudsString: String): Clouds {
        val type = object : TypeToken<Clouds>() {}.type
        return Gson().fromJson(cloudsString, type)
    }

    @TypeConverter
    fun fromSys(sys: Sys): String {
        return Gson().toJson(sys)
    }

    @TypeConverter
    fun toSys(sysString: String): Sys {
        val type = object : TypeToken<Sys>() {}.type
        return Gson().fromJson(sysString, type)
    }
}

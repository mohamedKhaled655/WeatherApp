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
    private val gson = Gson()

    @TypeConverter
    fun fromCoord(coord: Coord?): String? = gson.toJson(coord)

    @TypeConverter
    fun toCoord(coordString: String?): Coord? =
        coordString?.let { gson.fromJson(it, Coord::class.java) }


    @TypeConverter
    fun fromWeatherList(weatherList: List<Weather>?): String? = gson.toJson(weatherList)

    @TypeConverter
    fun toWeatherList(weatherListString: String?): List<Weather>? =
        gson.fromJson(weatherListString, object : TypeToken<List<Weather>>() {}.type)


    @TypeConverter
    fun fromMain(main: Main?): String? = gson.toJson(main)

    @TypeConverter
    fun toMain(mainString: String?): Main? =
        mainString?.let { gson.fromJson(it, Main::class.java) }


    @TypeConverter
    fun fromWind(wind: Wind?): String? = gson.toJson(wind)

    @TypeConverter
    fun toWind(windString: String?): Wind? =
        windString?.let { gson.fromJson(it, Wind::class.java) }


    @TypeConverter
    fun fromClouds(clouds: Clouds?): String? = gson.toJson(clouds)

    @TypeConverter
    fun toClouds(cloudsString: String?): Clouds? =
        cloudsString?.let { gson.fromJson(it, Clouds::class.java) }


    @TypeConverter
    fun fromSys(sys: Sys?): String? = gson.toJson(sys)

    @TypeConverter
    fun toSys(sysString: String?): Sys? =
        sysString?.let { gson.fromJson(it, Sys::class.java) }
}

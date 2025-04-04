package com.example.weatherapp.data.local

import androidx.compose.runtime.mutableStateOf
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocationType
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeLocalDataSource(private val initialFavLocations: List<FavoriteLocationEntity> = emptyList()): LocalDataSource{

    private val favLocationsFlow = MutableStateFlow(initialFavLocations.toMutableList())

    override suspend fun getAllFavLocations(): Flow<List<FavoriteLocationEntity>> {
        return favLocationsFlow.asStateFlow()
    }

    override suspend fun insertFavLocation(favLocation: FavoriteLocationEntity): Long {
        val updatedList = favLocationsFlow.value.toMutableList().apply { add(favLocation) }
        favLocationsFlow.value = updatedList
        return favLocation.id.toLong()
    }

    override suspend fun deleteFavLocation(favLocation: FavoriteLocationEntity?): Int {
        val updatedList = favLocationsFlow.value.toMutableList()
        val isRemoved = updatedList.remove(favLocation)
        favLocationsFlow.value = updatedList
        return if (isRemoved) 1 else 0
    }

    override suspend fun getAllWeathers(): Flow<List<CurrentWeatherModel>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherByLatLng(lat: Double, lon: Double): CurrentWeatherModel? {
        TODO("Not yet implemented")
    }

    override suspend fun insertWeather(weatherModel: CurrentWeatherModel): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteWeather(weatherModel: CurrentWeatherModel?): Int {
        TODO("Not yet implemented")
    }

    override suspend fun fetchAllAlerts(): Flow<List<WeatherAlert>> {
        TODO("Not yet implemented")
    }

    override suspend fun addNewAlert(alert: WeatherAlert): Long {
        TODO("Not yet implemented")
    }

    override suspend fun removeAlert(alertEntity: WeatherAlert?): Int {
        TODO("Not yet implemented")
    }

    override fun getLocationType(): LocationType {
        TODO("Not yet implemented")
    }

    override fun saveLocationType(locationType: LocationType) {
        TODO("Not yet implemented")
    }

    override fun getTemperatureUnit(): TempUnit {
        TODO("Not yet implemented")
    }

    override fun saveTemperatureUnit(unit: TempUnit) {
        TODO("Not yet implemented")
    }

    override fun getWindSpeedUnit(): WindSpeedUnit {
        TODO("Not yet implemented")
    }

    override fun saveWindSpeedUnit(unit: WindSpeedUnit) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): Lang {
        TODO("Not yet implemented")
    }

    override fun saveLanguage(language: Lang) {
        TODO("Not yet implemented")
    }
}
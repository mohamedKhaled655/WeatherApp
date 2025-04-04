package com.example.weatherapp.home_screen

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.utils.*
import com.example.weatherapp.utils.Response
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: WeatherRepository): ViewModel() {

    private val currentWeatherState= MutableStateFlow<Response<CurrentWeatherModel>>(Response.Loading)
    val currentWeather = currentWeatherState.asStateFlow()

    private val forecastWeatherState= MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val forecastWeather = forecastWeatherState.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    ////sharedPref
    private val _locationType = MutableStateFlow(repo.getLocationType())
    val locationType: StateFlow<LocationType> = _locationType

    private val _tempUnit = MutableStateFlow(repo.getTemperatureUnit())
    val tempUnit: StateFlow<TempUnit> = _tempUnit

    private val _windSpeedUnit = MutableStateFlow(repo.getWindSpeedUnit())
    val windSpeedUnit: StateFlow<WindSpeedUnit> = _windSpeedUnit

    private val _language = MutableStateFlow(repo.getLanguage())
    val language: StateFlow<Lang> = _language

    fun updateSettings() {
        _locationType.value = repo.getLocationType()
        _tempUnit.value = repo.getTemperatureUnit()
        _windSpeedUnit.value = repo.getWindSpeedUnit()
        _language.value = repo.getLanguage()
    }
    /////////

    fun getCurrentWeather(lat: Double, long: Double){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result=repo.getCurrentWeather(lat,long)

                result.collect{result ->
                    println("weather: ${result.name}")
                    repo.insertWeather(result)
                    currentWeatherState.value = Response.Success(result)

                    if(repo.insertWeather(result)>0){
                        println("Inserting weather: $result")
                        println("succ!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    }else{
                        repo.getWeatherByLatLng(lat,long)
                        println("!!!!!!!!!!!!!!!!!!!!!!!!!${repo.getWeatherByLatLng(lat,long)?.name}")
                    }

                }
                
            }catch (ex:Exception){
                currentWeatherState.value=Response.Failure(ex)
                _message.value = "An error occurred ${ex.message}"
            }finally {
                _isRefreshing.value = false
            }
        }
    }

    fun getForecastWeather(lat: Double, long: Double){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result=repo.getForecast(lat,long)
                result
                    .catch {
                        _message.value = "An error occurred ${it.message}"
                    }
                    .collect{
                        forecastWeatherState.value= Response.Success<WeatherResponse>(it)
                    }
            }catch (ex:Exception){
                forecastWeatherState.value=Response.Failure(ex)
                _message.value = "An error occurred ${ex.message}"
            }finally {
                _isRefreshing.value = false
            }
        }
    }

    fun refreshWeather(lat: Double, long: Double) {
        _isRefreshing.value = true
        getCurrentWeather(lat, long)
        getForecastWeather(lat, long)
    }



}

class HomeFactory(private val repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}
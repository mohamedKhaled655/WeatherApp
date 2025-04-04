package com.example.weatherapp.home_screen

import android.content.Context
import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.R
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

    fun getCurrentWeather(context: Context, lat: Double, long: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!NetworkUtils(context).isNetworkAvailable(context)) {
                try {
                    repo.getAllWeathers().collect { weathers ->
                        if (weathers.isNotEmpty()) {
                            val weather = weathers.find {
                                it.coord_lat.toInt() == lat.toInt() && it.coord_lat.toInt() == long.toInt()
                            } ?: weathers.first()

                            currentWeatherState.value = Response.Success(weather)

                            _message.value =
                                context.getString(R.string.no_internet_connection_showing_cached_data)
                        } else {
                            _message.value = context.getString(R.string.no_local_data_available)
                            currentWeatherState.value = Response.Failure(Exception(
                                context.getString(
                                    R.string.no_local_data
                                )))
                        }
                    }
                } catch (e: Exception) {
                    currentWeatherState.value = Response.Failure(e)

                    _message.value =
                        context.getString(R.string.failed_to_load_local_data, e.message)
                }

            }

            try {
                val result = repo.getCurrentWeather(lat, long)
                result.collect { weather ->
                    repo.insertWeather(weather)
                    currentWeatherState.value = Response.Success(weather)
                   // _message.value = context.getString(R.string.online_weather_updated)
                    
                }
            } catch (ex: Exception) {
                _message.value = "An error occurred ${ex.message}"
                currentWeatherState.value = Response.Failure(ex)
            } finally {
                _isRefreshing.value = false
            }
        }
    }


    /*fun getCurrentWeather(lat: Double, long: Double){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result=repo.getCurrentWeather(lat,long)

                result.collect{result ->
                    println("weather: ${result.name}")
                    val isSaved= repo.insertWeather(result)
                    currentWeatherState.value = Response.Success(result)

                    if(isSaved>0){
                        println("Inserting weather: $result")
                        println("succ!!!!!!!!!!!!!!!!!!!!!!!!!!!")
                    }else{
                        repo.getAllWeathers().collect{result->
                            if (result.isNotEmpty()) {
                                println("weather!!!!!!!!!: ${result[0].name}")
                            }

                            }
                        println("!!!!!!!!!!!!!!!!!!!!!!!!!${repo.getWeatherByLatLng(lat,long)?.name}")

                    }

                }

            }catch (ex:Exception){

                _message.value = "An error occurred ${ex.message}"
                val localWeather = repo.getWeatherByLatLng(lat, long)
                if (localWeather != null) {
                    currentWeatherState.value = Response.Success(localWeather)
                } else {
                    currentWeatherState.value = Response.Failure(ex)
                }
            }finally {
                _isRefreshing.value = false
            }
        }
    }*/

    private suspend fun saveWeatherToLocal(weather: CurrentWeatherModel, lat: Double, long: Double) {
        try {
            if (repo.insertWeather(weather) <= 0) {
                val localWeather = repo.getWeatherByLatLng(lat, long)
                localWeather?.let {
                    currentWeatherState.value = Response.Success(it)
                }
            }
        } catch (e: Exception) {
            _message.value = "Local save failed: ${e.message}"
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

    fun refreshWeather(context: Context, lat: Double, long: Double) {
        if (_isRefreshing.value) return
        _isRefreshing.value = true
        getCurrentWeather(context,lat, long)
        getForecastWeather(lat, long)
    }

    fun clearMessage() {
        _message.value = null
    }



}

class HomeFactory(private val repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}
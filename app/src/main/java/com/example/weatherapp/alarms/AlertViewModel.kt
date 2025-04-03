package com.example.weatherapp.alarms

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.example.weatherapp.data.models.AlertEntity
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.data.repo.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AlertViewModel (private val repo: WeatherRepository,private val application: Application) :ViewModel() {

    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alarms: StateFlow<List<WeatherAlert>> = _alerts

    init {
        getWeatherAlerts()
    }
    fun getWeatherAlerts() {
        viewModelScope.launch {

            repo.fetchAllAlerts().collect { alertList ->
                _alerts.value = alertList
               // println("1111111111111111111111111111111@${_alerts.value[0].startTime}")
            }
        }
    }
    fun addAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repo.addNewAlert(alert)
            scheduleWeatherAlert(application.applicationContext, alert)

        }
    }

    fun removeAlert(alert: WeatherAlert) {
        viewModelScope.launch {
            repo.removeAlert(alert)
            WorkManager.getInstance(application.applicationContext)
                .cancelAllWorkByTag(alert.id.toString())

        }
    }



}

class AlertFactory(
    private val repo: WeatherRepository,
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(AlertViewModel::class.java)) {
           AlertViewModel(repo, application) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
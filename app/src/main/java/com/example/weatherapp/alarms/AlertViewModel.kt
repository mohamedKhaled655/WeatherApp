package com.example.weatherapp.alarms

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
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
           // scheduleNotificationWithWorkManager(application.applicationContext, alert)
           // scheduleWeatherAlert(application.applicationContext, alert)

        }
    }

    fun removeAlert(alert: WeatherAlert) {
        viewModelScope.launch {

            cancelWeatherAlert(application.applicationContext, alert.id )
            repo.removeAlert(alert)


            WorkManager.getInstance(application.applicationContext)
                .cancelAllWorkByTag(alert.id.toString())

        }
    }

     /*fun sendWeatherAlert(alert: WeatherAlert) {
        val channelId = "weather_alerts"
        val notificationManager = application.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val notification = NotificationCompat.Builder(application.applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(alert.type)
            .setContentText("description")
            .setSound(alarmSound)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(alert.id, notification)
    }*/



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
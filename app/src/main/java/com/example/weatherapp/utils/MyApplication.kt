package com.example.weatherapp.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.weatherapp.alarms.NotificationHelper
import com.example.weatherapp.data.local.SharedPreferencesHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferencesHelper.init(this)

        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

    }
}
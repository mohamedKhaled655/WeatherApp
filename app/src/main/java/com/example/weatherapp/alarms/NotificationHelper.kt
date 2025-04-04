package com.example.weatherapp.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.weatherapp.MainActivity
import com.example.weatherapp.R
import com.example.weatherapp.data.models.WeatherAlert

class NotificationHelper(private val context: Context) {


     fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //val manager = getSystemService(NotificationManager::class.java)
            val manager = context.getSystemService(NotificationManager::class.java)
            val existingChannel = manager.getNotificationChannel("channel_id")

            if (existingChannel == null) {
                val channel = NotificationChannel(
                    "channel_id",
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for weather alerts"
                    enableLights(true)
                    enableVibration(true)
                }
                manager.createNotificationChannel(channel)
            }
        }
    }


    /*fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(NotificationManager::class.java)

            // Create notification channel
            createChannel(
                notificationManager,
                NOTIFICATION_CHANNEL_ID,
                "Weather Notifications",
                "Standard weather alerts and notifications",
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                AudioAttributes.USAGE_NOTIFICATION
            )

            // Create alarm channel

        }
    }

    private fun createChannel(
        manager: NotificationManager,
        channelId: String,
        name: String,
        description: String,
        soundUri: android.net.Uri,
        usage: Int
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(usage)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
                setSound(soundUri, audioAttributes)
            }

            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(alert: WeatherAlert) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("ALERT_ID", alert.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Determine which sound type to use based on alert configuration
        *//*val soundUri = if (alert.isNotification) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }*//*

        // Determine which channel to use based on alert configuration
        val channelId = if (alert.isNotification) NOTIFICATION_CHANNEL_ID else ALARM_CHANNEL_ID

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Weather Alert: ${alert.type}")
            .setContentText("Click to open the app and check details.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // For pre-Oreo devices, we need to set the sound directly on the notification
        *//*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builder.setSound(soundUri)
        }*//*

        notificationManager.notify(alert.id, builder.build())
    }*/
}
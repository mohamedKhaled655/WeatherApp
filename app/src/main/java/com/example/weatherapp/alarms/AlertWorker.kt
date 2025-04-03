package com.example.weatherapp.alarms

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherapp.R
import com.example.weatherapp.data.models.WeatherAlert
import java.util.concurrent.TimeUnit
import android.net.Uri

import android.provider.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlertWorker(
    val context: Context,
    workParams: WorkerParameters
) : CoroutineWorker(context, workParams) {

    override suspend fun doWork(): Result {
        val alertId = inputData.getInt("ALERT_ID", 0)
        val alertType = inputData.getString("ALERT_TYPE") ?: "notification"
        val startTime = inputData.getLong("START_TIME", System.currentTimeMillis())

        if (alertType == "notification") {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.sendNotification(
                WeatherAlert(
                    id = alertId,
                    type = alertType,
                    startTime = startTime,
                    endTime = startTime + 3600000,
                    isNotification = true,
                    isActive = true
                )
            )
        } else {
            playAlarmSound()
        }





        return Result.success()
    }

    private fun playAlarmSound() {
        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        val ringtone = RingtoneManager.getRingtone(applicationContext, ringtoneUri)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ringtone.isLooping = false
        }

        ringtone.play()


        CoroutineScope(Dispatchers.Main).launch {
            delay(30_000)
            ringtone.stop()
        }
    }

}





fun scheduleWeatherAlert(context: Context, alert: WeatherAlert) {
    val workManager = WorkManager.getInstance(context)


    val delay = maxOf(alert.startTime - System.currentTimeMillis(), 0)
    val data = workDataOf(
        "ALERT_ID" to alert.id,
        "ALERT_TYPE" to alert.type,
        "START_TIME" to alert.startTime
    )

    val request = OneTimeWorkRequestBuilder<AlertWorker>()
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(data)
        .addTag(alert.id.toString())
        .build()

    workManager.enqueue(request)
}










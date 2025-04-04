package com.example.weatherapp.alarms



import android.Manifest
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.models.WeatherAlert
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import java.util.concurrent.TimeUnit

class NotificationWorker(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        private var ringtonePlayer: Ringtone? = null
        private val handler = Handler(Looper.getMainLooper())

        fun stopAlarmSound() {
            ringtonePlayer?.stop()
            ringtonePlayer = null
        }
    }

    override fun doWork(): Result {



        val alarmType = inputData.getString("alarmType") ?: "Notification"
        val alertId = inputData.getInt("ALERT_ID", 0)
        val startTime = inputData.getLong("START_TIME", System.currentTimeMillis())

        if (alarmType == "Notification") {
            sendNotification(
                WeatherAlert(
                    id = alertId,
                    type = alarmType,
                    startTime = startTime,
                    endTime = startTime + 3600000,
                    isNotification = true,
                    isActive = true
                )
            )
        } else {
            playAlarmSound()
            sendNotification(
                WeatherAlert(
                    id = alertId,
                    type = alarmType,
                    startTime = startTime,
                    endTime = startTime + 3600000,
                    isNotification = true,
                    isActive = true
                )
            )
        }

        return Result.success()
    }

    private fun sendNotification(alert: WeatherAlert) {
        val context = applicationContext
        val notificationManager = NotificationManagerCompat.from(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationWorker", "Permission denied for notifications.")
            return
        }

        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Weather Alert: ${alert.type}")
            .setContentText("Tap to open the app.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(1, builder.build())
    }

    private fun playAlarmSound() {
        try {
            if (ringtonePlayer?.isPlaying == true) {
                Log.d("NotificationWorker", "Alarm sound is already playing.")
                return
            }

            val ringtoneUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtonePlayer = RingtoneManager.getRingtone(applicationContext, ringtoneUri)
            ringtonePlayer?.play()


            handler.postDelayed({ stopAlarmSound() }, 30_000)
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error playing alarm sound", e)
        }
    }
}


fun scheduleNotificationWithWorkManager(context: Context,alert: WeatherAlert) {

    val delay = maxOf(alert.startTime - System.currentTimeMillis(), 0)
    val inputData = workDataOf(
        "ALERT_ID" to alert.id,
        "alarmType" to alert.type,
        "START_TIME" to alert.startTime
    )

    val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
        .setInitialDelay(delay, TimeUnit.SECONDS)
        .setInputData(
            inputData
        )
        .build()


    WorkManager.getInstance(context).enqueueUniqueWork(
        "weather_alert_work",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )

    Toast.makeText(context, "Weather Alert Set! :${delay} seconds", Toast.LENGTH_SHORT).show()
}

fun cancelWeatherAlert(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("weather_alert_work")
    Toast.makeText(context, "Weather Alert Canceled!", Toast.LENGTH_SHORT).show()
}

package com.example.weatherapp.alarms



import android.Manifest
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
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
import com.example.weatherapp.R
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
        val alarmType = inputData.getString("alarmType") ?: "Weather Alert"
        val desc = inputData.getString("DESC") ?: "description"
        val alertId = inputData.getInt("ALERT_ID", 0)
        val startTime = inputData.getLong("START_TIME", System.currentTimeMillis())
        val isNotification = inputData.getBoolean("IS_NOTIFICATION", true)
        val isActive = inputData.getBoolean("IS_ACTIVE", true)


        val alert = WeatherAlert(
            id = alertId,
            type = alarmType,
            startTime = startTime,
            endTime = startTime + 3600000,
            isNotification = isNotification,
            isActive = isActive,
            desc = desc
        )


        sendNotification(alert)


        if (!isNotification) {
            playAlarmSound()
        }

        return Result.success()
    }

    private fun sendNotification(alert: WeatherAlert) {
        val context = applicationContext
        val notificationManager = NotificationManagerCompat.from(context)


        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)


        intent?.putExtra("ALERT_ID", alert.id)


        val pendingIntent = PendingIntent.getActivity(
            context,
            alert.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("NotificationWorker", "Permission denied for notifications.")
            return
        }

        val builder = NotificationCompat.Builder(context, "channel_id")
            .setSmallIcon(R.drawable.notification)
            .setContentTitle("Weather Alert: ${alert.type}")
            .setContentText("${alert.desc}")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(alert.id, builder.build())
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


fun scheduleNotificationWithWorkManager(context: Context, alert: WeatherAlert) {
    val delay = maxOf(alert.startTime - System.currentTimeMillis(), 0)
    val inputData = workDataOf(
        "ALERT_ID" to alert.id,
        "alarmType" to alert.type,
        "START_TIME" to alert.startTime,
        "IS_NOTIFICATION" to alert.isNotification,
        "IS_ACTIVE" to alert.isActive,
        "DESC" to alert.desc,
    )

    val workRequest = OneTimeWorkRequest.Builder(NotificationWorker::class.java)
        .setInitialDelay(delay, TimeUnit.MILLISECONDS)
        .setInputData(inputData)
        .build()

    WorkManager.getInstance(context).enqueueUniqueWork(
        "weather_alert_${alert.id}",
        ExistingWorkPolicy.REPLACE,
        workRequest
    )

    Toast.makeText(context, "Weather Alert Set! In: ${delay / 1000} seconds", Toast.LENGTH_SHORT).show()
}

fun cancelWeatherAlert(context: Context, alertId: Int) {
    WorkManager.getInstance(context).cancelUniqueWork("weather_alert_${alertId}")
    Toast.makeText(context, "Weather Alert Canceled!", Toast.LENGTH_SHORT).show()
}

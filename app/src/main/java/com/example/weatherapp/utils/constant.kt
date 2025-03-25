package com.example.weatherapp.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CustomIconImage(iconCode: String, size:Int, x: Int =2) {
    val iconUrl = remember { "https://openweathermap.org/img/wn/${iconCode}@${x}x.png" }

    GlideImage(
        model = iconUrl,
        contentDescription = "Weather Icon",
        modifier = Modifier.size(size.dp),
    )
}


fun Double.toCelsius(): String = String.format("%.2f", this - 273.15)

fun Double.toKmPerHour(): String = String.format("%.2f", this * 3.6)

fun Int.toMmHg(): String = String.format("%.2f", this * 0.75006)

fun Int.toHumidityPercentage(): String = "$this%"

fun Long.toFormattedDate(timezoneOffset: Int): String {
    val date = Date((this + timezoneOffset) * 1000)
    val sdf = SimpleDateFormat("EE, dd MMMM", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(date)
}

fun Long.toFormattedTime(timezoneOffset: Int): String {
    val date = Date((this + timezoneOffset) * 1000)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(date)
}

fun Long.toFormattedDay(timezoneOffset: Int): String {
    val date = Date((this + timezoneOffset) * 1000)
    val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("GMT")
    return sdf.format(date)
}
package com.example.weatherapp.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
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

@Composable
fun CustomLottieUrl(url:String,size:Int=50) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Url(url))
    LottieAnimation(
        modifier = Modifier.size(size.dp),
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

}


fun Double.toCelsius(): String = String.format("%.2f", this - 273.15)


fun Double.toFahrenheit(): String {
    return String.format("%.1f",(this - 273.15) * 9/5 + 32)
}

fun Double.toKelvin(): String {
    return String.format("%.1f",this)
}
fun convertTemperature(
    tempUnit: TempUnit,
    temperature: Double
) = when (tempUnit) {
    TempUnit.Celsius -> temperature.toCelsius()
    TempUnit.Fahrenheit -> temperature.toFahrenheit()
    TempUnit.Kelvin -> temperature.toKelvin()
}



fun Double.toMetersPerSecond(): Double {
    return this
}

fun Double.toMilesPerHour(): String {
    return String.format("%.2f",this * 2.23694)
}

fun convertWindSpeed(speedUnit: WindSpeedUnit,speed:Double)= when (speedUnit) {
    WindSpeedUnit.MeterPerSec -> speed.toMetersPerSecond()
    WindSpeedUnit.MilesPerHour->speed.toMilesPerHour()
}

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
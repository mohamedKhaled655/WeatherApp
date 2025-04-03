package com.example.weatherapp.utils

enum class LocationType { GPS, Map }
enum class TempUnit(val symbol: String="°C")
{
    Celsius("°C"),
    Fahrenheit("°F"),
    Kelvin("°K")
}
enum class WindSpeedUnit(val symbol: String="m/s") { MeterPerSec("m/s"), MilesPerHour("mph") }
enum class Lang { AR, EN }
package com.example.weatherapp.utils


enum class LocationType {
                        GPS, Map ;
    fun getDisplayName(language: Lang): String {
        return when (language) {
            Lang.AR -> when (this) {
                GPS -> "الموقع"
                Map -> "الخريطة"
            }
            Lang.EN -> when (this) {
                GPS -> "GPS"
                Map -> "Map"
            }
        }
    }
}
enum class TempUnit(val symbol: String="°C")
{
    Celsius("°C"),
    Fahrenheit("°F"),
    Kelvin("°K");

    fun getDisplayName(language: Lang): String {
        return when (language) {
            Lang.AR -> when (this) {
                Celsius -> "مئوي"
                Fahrenheit -> "فهرنهايت"
                Kelvin -> "كلفن"
            }
            Lang.EN -> when (this) {
                Celsius -> "Celsius"
                Fahrenheit -> "Fahrenheit"
                Kelvin -> "Kelvin"
            }
        }
    }

    fun getSymbol(language: Lang): String {
        return when (language) {
            Lang.AR -> when (this) {
                Celsius -> " س °"
                Fahrenheit -> " ف °"
                Kelvin -> " ك °"
            }
            Lang.EN -> when (this) {
                Celsius -> "°C"
                Fahrenheit -> "°F"
                Kelvin -> "°K"
            }
        }
    }

}
enum class WindSpeedUnit(val symbol: String="m/s") {
    MeterPerSec("m/s"), MilesPerHour("mph");
    fun getDisplayName(language: Lang): String {
        return when (language) {
            Lang.AR -> when (this) {
                MeterPerSec -> "متر / الثانية"
                MilesPerHour -> "ميل / الساعة"
            }
            Lang.EN -> when (this) {
                MeterPerSec -> "Meter / second"
                MilesPerHour -> "Miles / hour"
            }
        }
    }

    fun getSymbol(language: Lang): String {
        return when (language) {
            Lang.AR -> when (this) {
                MeterPerSec -> "متر/ث"
                MilesPerHour -> "ميل/ساعة"
            }
            Lang.EN -> when (this) {
                MeterPerSec -> "m/s"
                MilesPerHour -> "mph"
            }
        }
    }
}
enum class Lang {
                AR,
    EN;

    fun getDisplayName(language: Lang): String {
        return when (language) {
            Lang.AR -> when (this) {
                AR -> "العربية"
                EN -> "الإنجليزية"
            }
            Lang.EN -> when (this) {
                AR -> "Arabic"
                EN -> "English"
            }
        }
    }

    fun getCode(): String {
        return when(this) {
            AR -> "ar"
            EN -> "en"
        }
    }

}


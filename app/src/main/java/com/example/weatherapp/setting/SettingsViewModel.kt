package com.example.weatherapp.setting

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.favourites.FavouriteViewModel
import com.example.weatherapp.utils.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {
    private val _locationType = MutableStateFlow(repository.getLocationType())
    val locationType: StateFlow<LocationType> = _locationType

    private val _tempUnit = MutableStateFlow(repository.getTemperatureUnit())
    val tempUnit: StateFlow<TempUnit> = _tempUnit

    private val _windSpeedUnit = MutableStateFlow(repository.getWindSpeedUnit())
    val windSpeedUnit: StateFlow<WindSpeedUnit> = _windSpeedUnit

    private val _language = MutableStateFlow(repository.getLanguage())
    val language: StateFlow<Lang> = _language

    fun updateLocationType(locationType: LocationType) {
        viewModelScope.launch {
            repository.saveLocationType(locationType)
            _locationType.value = locationType
        }
    }

    fun updateTemperatureUnit(unit: TempUnit) {
        viewModelScope.launch {
            repository.saveTemperatureUnit(unit)
            _tempUnit.value = unit
        }
    }

    fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        viewModelScope.launch {
            repository.saveWindSpeedUnit(unit)
            _windSpeedUnit.value = unit
        }
    }

    fun updateLanguage(language: Lang) {
        viewModelScope.launch {
            repository.saveLanguage(language)
            _language.value = language
        }
    }
}

class SettingsFactory(private val repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repo) as T
    }
}
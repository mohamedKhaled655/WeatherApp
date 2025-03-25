package com.example.weatherapp.home_screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build

import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import com.example.weatherapp.data.models.LocationModel
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class LocationViewModel(private val context: Context) : ViewModel() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
     var locationState: MutableState<Location> = mutableStateOf(Location(""))
     var addressState: MutableState<String> =  mutableStateOf("")

    init {
        getFreshLocation()
    }


    @SuppressLint("MissingPermission")
    fun getFreshLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(5000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)

                    locationState.value = location.lastLocation ?: Location("")
                    getAddressFromLocation(locationState.value)

                }
            },
            Looper.myLooper()
        )
    }


    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(context, Locale.getDefault())

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                addressState.value = if (addresses.isNotEmpty()) {
                    addresses[0].getAddressLine(0) ?: ""
                } else {
                    ""
                }
            }
        } else {
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            addressState.value = if (addresses != null && addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0) ?: ""
            } else {
                ""
            }
        }
    }


}
class LocationViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LocationViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

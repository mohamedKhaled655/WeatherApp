package com.example.weatherapp.favourites

import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.data.repo.WeatherRepository
import com.example.weatherapp.utils.Response
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.Locale

class FavouriteViewModel(private val repo: WeatherRepository) :ViewModel(){

    private val _favLocations = MutableStateFlow<Response<List<FavoriteLocationEntity>>>(Response.Loading)
    val favLocations = _favLocations.asStateFlow()

    private val _locationDetails = MutableStateFlow<FavoriteLocationEntity?>(null)
    val locationDetails: StateFlow<FavoriteLocationEntity?> = _locationDetails.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message
    fun getFavLocations(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result=repo.getAllFavLocations()
                result
                    .catch {
                            ex->_favLocations.value=Response.Failure(ex)
                        println(ex.message)
                    }
                    .collect{
                        if (it != null) {
                            _favLocations.value= Response.Success<List<FavoriteLocationEntity>>(it)
                        }else {
                            _message.value = "Please try again later"
                        }
                    }
            }catch (ex:Exception){
                _favLocations.value=Response.Failure(ex)
                _message.value = "An error occurred: ${ex.message}"            }
        }
    }

   /* fun removeLocationFromFav(favoriteLocationEntity: FavoriteLocationEntity?){
        if (favoriteLocationEntity!=null){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result=repo.removeFavLocation(favoriteLocationEntity)
                    if(result>0){
                        // getFavProducts()
                        _message.value = "Removed Location Successfully"
                    }else{
                        _message.value = "this Location is not in favorites"
                    }
                }catch (ex:Exception){
                    println(ex.message)
                    _message.value = "An error occurred: ${ex.message}"
                }
            }
        }else{
            _message.value = "Could not remove Location, missing data"
        }
    }*/

    fun removeLocationFromFav(location: FavoriteLocationEntity?){
        if (location == null) {
            _message.value = "Could not add Location, missing data"
            return
        }

        viewModelScope.launch {
            val result = repo.removeFavLocation(location)
            if (result > 0) {
                _message.value = "Removed Location Successfully"
            } else {
                _message.value = "this Location is not in favorites"
            }
        }
    }

    fun addLocationToFav(location: FavoriteLocationEntity?) {
        if (location == null) {
            _message.value = "Could not add Location, missing data"
            return
        }

        viewModelScope.launch {
            val result = repo.addFavLocation(location)
            if (result > 0) {
                _message.value = "Added Location Successfully"
            } else {
                _message.value = "this Location is not in favorites"
            }
        }
    }

   /* fun addLocationToFav(favoriteLocationEntity: FavoriteLocationEntity?){
        if (favoriteLocationEntity!=null){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result=repo.addFavLocation(favoriteLocationEntity)
                    if(result>0){
                        // getFavProducts()
                        _message.value = "Added Location Successfully"
                    }else{
                        _message.value = "this Location is not in favorites"
                    }
                }catch (ex:Exception){
                    _message.value = "An error occurred: ${ex.message}"
                }
            }
        }else{
            _message.value = "Could not add Location, missing data"
        }
    }*/



    fun fetchLocationDetailFromLatLng(context: Context, latLng: LatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

                val locationEntity = if (addresses.isNullOrEmpty()) {
                    FavoriteLocationEntity(country = "Unknown Country", name = "Unknown City", latitude = 0.0, longitude = 0.0)
                } else {
                    val address = addresses[0]
                    FavoriteLocationEntity(
                        country = address.countryName ?: "Unknown Country",
                        name = address.locality ?: address.subAdminArea ?: "Unknown City",
                        latitude = address.latitude,
                        longitude = address.longitude
                    )
                }

                _locationDetails.value = locationEntity
            } catch (e: Exception) {
                Log.e("MapError", "Error getting location details", e)
                _locationDetails.value = FavoriteLocationEntity(country = "Unknown Country", name = "Unknown City", latitude = 0.0, longitude = 0.0)
            }
        }
    }

    fun clearMessage() {
        _message.value = ""
    }


}

class FavLocationFactory(private val repo: WeatherRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteViewModel(repo) as T
    }
}
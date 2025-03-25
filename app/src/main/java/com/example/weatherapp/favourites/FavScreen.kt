package com.example.weatherapp.favourites

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weatherapp.data.models.FavoriteLocation
import com.example.weatherapp.ui.theme.DarkBlue
import com.example.weatherapp.ui.theme.Spacing
import com.example.weatherapp.utils.ScreenRoute
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale


@Composable
fun FavouriteScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Fav")

        }

        FloatingActionButton(
            onClick = {
                Toast.makeText(context, "Maps", Toast.LENGTH_SHORT).show()
                navHostController.navigate(ScreenRoute.MapScreenRoute)
            },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(vertical = 120.dp, horizontal = 24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
    }
}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 3f) // Zoom to show countries
    }
    val selectedLocation = remember { mutableStateOf(LatLng(30.0444, 31.2357)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navHostController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding() + 60.dp // Consider Bottom Nav Bar
                )
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = false,zoomGesturesEnabled = true),
                onMapClick = { latLng ->
                    selectedLocation.value = latLng
                    val address = getLocationDetailFromLatLng(context, latLng)
                    Toast.makeText(context, "Selected: ${address.country}", Toast.LENGTH_SHORT).show()
                }
            ) {
                Marker(
                    state = MarkerState(position = selectedLocation.value),
                    title = "Selected Location",
                    snippet = getLocationDetailFromLatLng(context, selectedLocation.value).name
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .7f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Country: ${getLocationDetailFromLatLng(context, selectedLocation.value).country}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "City: ${getLocationDetailFromLatLng(context, selectedLocation.value).name}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ElevatedButton(
                            onClick = {
                                val locationDetails = getLocationDetailFromLatLng(context, selectedLocation.value)
                                Toast.makeText(
                                    context,
                                    "Selected Location: ${locationDetails.country}, ${locationDetails.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navHostController.popBackStack()
                            },
                            modifier = Modifier.fillMaxWidth().border(1.dp, MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 0.dp),
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.White,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text("Confirm Location")
                        }
                    }
                }


            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}






fun getLocationDetailFromLatLng(context: Context, latLng: LatLng): FavoriteLocation {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        if (addresses.isNullOrEmpty()) {
            return FavoriteLocation(country =  "Unknown Country", name = "Unknown City", latitude = 0.0, longitude = 0.0)
        }

        val address = addresses[0]
        FavoriteLocation(
            country = address.countryName ?: "Unknown Country",
            name = address.locality ?: address.subAdminArea ?: "Unknown City",
            latitude = address.latitude,
            longitude = address.longitude,

        )
    } catch (e: Exception) {
        Log.e("MapError", "Error getting location details", e)
        FavoriteLocation(country =  "Unknown Country", name = "Unknown City", latitude = 0.0, longitude = 0.0)    }
}


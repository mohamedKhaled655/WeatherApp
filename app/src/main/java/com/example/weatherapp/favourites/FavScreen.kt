package com.example.weatherapp.favourites

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.ui.theme.Spacing
import com.example.weatherapp.utils.Response
import com.example.weatherapp.utils.ScreenRoute
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun FavouriteScreen(navHostController: NavHostController,viewModel: FavouriteViewModel) {
    LaunchedEffect(Unit) {
        viewModel.getFavLocations()
    }
    val locationState = viewModel.favLocations.collectAsState()
    val messageState = viewModel.message.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var recentlyDeletedItem: FavoriteLocationEntity? by remember { mutableStateOf(null) }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            .windowInsetsPadding(WindowInsets.navigationBars),
            bottomBar = {},
        snackbarHost = {

                SnackbarHost(
                    hostState = snackBarHostState,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        .padding(bottom = 80.dp)) }


    )
    { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background))
        {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (val state = locationState.value) {
                    is Response.Loading -> {
                        CircularProgressIndicator()
                    }
                    is Response.Success -> {

                        if (state.data.isEmpty()) {
                            Text(
                                text = "No favorite locations yet.",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .windowInsetsPadding(WindowInsets.statusBars)
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                items(state.data.size) { index ->
                                    FavItem(state.data[index],navHostController){
                                        recentlyDeletedItem = state.data[index]
                                        viewModel.removeLocationFromFav(state.data[index])
                                    }
                                    Spacer(Modifier.padding(vertical = 8.dp))
                                    HorizontalDivider()
                                }
                            }
                        }

                    }
                    is Response.Failure -> {
                        Text(
                            text = "Error: ${state.error.message}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

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
    LaunchedEffect(messageState.value) {
        if (messageState.value == "Removed Location Successfully") {
            val result = snackBarHostState.showSnackbar(
                message = messageState.value.toString(),
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )

            if (result == SnackbarResult.ActionPerformed) {
                recentlyDeletedItem?.let { viewModel.addLocationToFav(it) }
                recentlyDeletedItem = null
            }
            viewModel.clearMessage()
        }


    }

}


@Composable
fun FavItem(model: FavoriteLocationEntity,navHostController: NavHostController, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable {

                navHostController.navigate(ScreenRoute.DetailsFavouriteScreenRoute(model.latitude,model.longitude))

            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,

    ) {
        Text(
            text = model.country,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = model.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        IconButton(onClick = {
            onDelete()
        }) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navHostController: NavHostController,viewModel: FavouriteViewModel) {
    val locationState = viewModel.locationDetails.collectAsState()
    val messageState = viewModel.message.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(30.0444, 31.2357), 4f)
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
                    bottom = paddingValues.calculateBottomPadding() + 60.dp
                )
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = true),
                uiSettings = MapUiSettings(zoomControlsEnabled = false,zoomGesturesEnabled = true),
                onMapClick = { latLng ->
                    selectedLocation.value = latLng
                    val address = viewModel.fetchLocationDetailFromLatLng(context, latLng)
                    Toast.makeText(context, "Selected: ${locationState.value?.country}", Toast.LENGTH_SHORT).show()
                }
            ) {
                Marker(
                    state = MarkerState(position = selectedLocation.value),
                    title = "Selected Location",
                    snippet = locationState.value?.name
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
                            text = "Country: ${locationState.value?.country}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "City: ${locationState.value?.name}",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ElevatedButton(
                            onClick = {
                                val locationDetails = locationState.value
                                viewModel.addLocationToFav(locationDetails)
                                Toast.makeText(
                                    context,
                                    "Selected Location: ${locationDetails?.country}, ${locationDetails?.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                navHostController.popBackStack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                ),
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









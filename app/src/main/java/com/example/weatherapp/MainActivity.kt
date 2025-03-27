package com.example.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weatherapp.alarms.AlarmScreen
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.favourites.FavLocationFactory
import com.example.weatherapp.favourites.FavouriteScreen
import com.example.weatherapp.favourites.MapScreen
import com.example.weatherapp.home_screen.HomeFactory
import com.example.weatherapp.home_screen.HomeScreen
import com.example.weatherapp.home_screen.LocationViewModel
import com.example.weatherapp.home_screen.LocationViewModelFactory
import com.example.weatherapp.setting.SettingScreen
import com.example.weatherapp.ui.theme.DarkGradientColors
import com.example.weatherapp.ui.theme.LightGradientColors
import com.example.weatherapp.ui.theme.Spacing
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utils.ScreenRoute
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale
import kotlin.math.log

class MainActivity : ComponentActivity() {
    private  val TAG = "MainActivity"
    lateinit var navHostController: NavHostController

  lateinit var locationViewModel: LocationViewModel
    lateinit var locationState:MutableState<Location>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navHostController= rememberNavController()
            WeatherAppTheme (){
                //SetUpNavHost()
                AppScreen()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        if (checkPermissions()) {
            if (isLocationEnabled()) {
               // locationViewModel.getFreshLocation()
            } else {
                enableLocationServices()
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                100
            )
        }
    }

    fun checkPermissions(): Boolean{
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    /*@SuppressLint("MissingPermission")
    fun getFreshLocation(){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(
            LocationRequest.Builder(5000).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(location: LocationResult) {
                    super.onLocationResult(location)
                    Log.i(TAG, "onLocationResult: ------${location.locations}")
                    locationState.value = location.lastLocation ?: Location("")
                    getAddressFromLocation(locationState.value)

                }
            },
            Looper.myLooper()
        )
    }


    private fun getAddressFromLocation(location: Location) {
        val geocoder = Geocoder(this, Locale.getDefault())

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
    }*/

    fun enableLocationServices(){
        Toast.makeText(this,"Turn On location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    fun isLocationEnabled(): Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (isLocationEnabled()) {
                    locationViewModel.getFreshLocation()
                } else {
                    enableLocationServices()
                }
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @Composable
    fun AppScreen() {
        //val navController = rememberNavController()
        Scaffold(
            Modifier.background(MaterialTheme.colorScheme.background),
           containerColor = MaterialTheme.colorScheme.background,

            bottomBar = {
            BottomBar(navHostController) },

        ) { it ->
            Log.i("TAG", "AppScreen: $it")
            SetUpNavHost()
        }
    }

    @Composable
    fun BottomBar(navHostController: NavHostController) {
        val listOfScreens = arrayOf(
            ScreenRoute.HomeScreenRoute,
            ScreenRoute.FavouriteScreenRoute,
            ScreenRoute.NotificationScreenRoute,
            ScreenRoute.SettingScreenRoute,

        )
        val navBackStackEntry = navHostController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry.value?.destination
        val selectedItem = remember { mutableStateOf(0) }
        NavigationBar(

            containerColor =MaterialTheme.colorScheme.background
        ) {
            listOfScreens.forEachIndexed { index, item ->
                NavigationBarItem(

                    icon = {
                        Icon(
                            painterResource(item.icon),
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    selected = selectedItem.value == index,
                    onClick = {
                        selectedItem.value = index

                        navHostController.navigate(item) {
                            popUpTo(navHostController.graph.findStartDestination().id)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun SetUpNavHost() {

        NavHost(
            navController = navHostController,
            startDestination = ScreenRoute.HomeScreenRoute,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<ScreenRoute.HomeScreenRoute> {
                HomeScreen(viewModel(factory = HomeFactory(
                    WeatherRepositoryImpl.getInstance(
                        WeatherRemoteDataSource(RetrofitHelper.apiService),
                        WeatherLocalDataSource(WeatherDatabase.getInstance(this@MainActivity).getWeatherDao())

                    )
                )),
                    viewModel(factory = LocationViewModelFactory(
                        this@MainActivity
                    ))
                )
            }

            composable<ScreenRoute.FavouriteScreenRoute> {
                FavouriteScreen(
                    navHostController,
                    viewModel(
                        factory = FavLocationFactory(
                            WeatherRepositoryImpl.getInstance(
                                WeatherRemoteDataSource(RetrofitHelper.apiService),
                                WeatherLocalDataSource(WeatherDatabase.getInstance(this@MainActivity).getWeatherDao())

                            )
                        )
                    )
                    )
            }
            composable<ScreenRoute.MapScreenRoute> {
                MapScreen(
                    navHostController,
                    viewModel(
                        factory = FavLocationFactory(
                            WeatherRepositoryImpl.getInstance(
                                WeatherRemoteDataSource(RetrofitHelper.apiService),
                                WeatherLocalDataSource(WeatherDatabase.getInstance(this@MainActivity).getWeatherDao())

                            )
                        )
                    )
                    )
            }
            composable<ScreenRoute.NotificationScreenRoute> {
                AlarmScreen()
            }
            composable<ScreenRoute.SettingScreenRoute> {
                SettingScreen()
            }
           /* composable<ScreenRoute.SearchScreenRoute>() {
                val searchViewModel: SearchViewModel = viewModel()
                SearchScreen(viewModel = searchViewModel)
            }
            composable<ScreenRoute.AllProductsScreenRoute> {
                AllProductsScreen(
                    viewModel(factory = AllProductFactory(
                        ProductRepositoryImpl.getInstance(
                            ProductsRemoteDataSource(RetrofitHelper.apiService),
                            ProductLocalDataSource(ProductDatabase.getInstance(this@MainActivity).getProductDao())
                        )
                    ))
                )
            }
            composable<ScreenRoute.AllFavScreenRoute> {
                FavScreen(
                    viewModel(factory = FavProductFactory(
                        ProductRepositoryImpl.getInstance(
                            ProductsRemoteDataSource(RetrofitHelper.apiService),
                            ProductLocalDataSource(ProductDatabase.getInstance(this@MainActivity).getProductDao())
                        )
                    ))
                )
            }*/
        }
    }
}


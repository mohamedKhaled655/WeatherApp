package com.example.weatherapp


import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.work.WorkManager
import com.example.weatherapp.alarms.AlarmScreen
import com.example.weatherapp.alarms.AlertFactory
import com.example.weatherapp.data.local.WeatherDatabase
import com.example.weatherapp.data.local.WeatherLocalDataSource
import com.example.weatherapp.data.remote.RetrofitHelper
import com.example.weatherapp.data.remote.WeatherRemoteDataSource
import com.example.weatherapp.data.repo.WeatherRepositoryImpl
import com.example.weatherapp.favourites.DetailsFavScreen
import com.example.weatherapp.favourites.FavLocationFactory
import com.example.weatherapp.favourites.FavouriteScreen
import com.example.weatherapp.favourites.MapScreen
import com.example.weatherapp.home_screen.HomeFactory
import com.example.weatherapp.home_screen.HomeScreen
import com.example.weatherapp.home_screen.LocationViewModel
import com.example.weatherapp.home_screen.LocationViewModelFactory
import com.example.weatherapp.setting.SettingScreen
import com.example.weatherapp.setting.SettingsFactory
import com.example.weatherapp.setting.SettingsViewModel
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.NetworkUtils
import com.example.weatherapp.utils.ScreenRoute

class MainActivity : ComponentActivity() {
    private  val TAG = "MainActivity"
    private lateinit var networkObserver: NetworkUtils

    lateinit var navHostController: NavHostController

  lateinit var locationViewModel: LocationViewModel
    lateinit var locationState:MutableState<Location>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        networkObserver = NetworkUtils(this)
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

    override fun onResume() {
        super.onResume()
        networkObserver.checkConnectionStatus()
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


    ///not




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

            ) { innerPadding ->
            Log.i("TAG", "AppScreen: $innerPadding")
            SetUpNavHost(innerPadding)
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
                   // label = { Text(item.title) },
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
    fun SetUpNavHost(innerPadding: PaddingValues) {
        val repoForGetWeatherDao=WeatherRepositoryImpl.getInstance(
            WeatherRemoteDataSource(RetrofitHelper.apiService),
            WeatherLocalDataSource(
                WeatherDatabase.getInstance(this@MainActivity).getWeatherDao(),
                WeatherDatabase.getInstance(this@MainActivity).getHomeWeatherDao(),
                //WeatherDatabase.getInstance(this@MainActivity).weatherAlertDao(),
                WeatherDatabase.getInstance(this@MainActivity).alertDao())

        )

        val favFactory=FavLocationFactory(repoForGetWeatherDao)

        val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsFactory(
            WeatherRepositoryImpl.getInstance(
                WeatherRemoteDataSource(RetrofitHelper.apiService),
                WeatherLocalDataSource(
                    WeatherDatabase.getInstance(this@MainActivity).getWeatherDao(),
                    WeatherDatabase.getInstance(this@MainActivity).getHomeWeatherDao(),
                    WeatherDatabase.getInstance(this@MainActivity).alertDao()
                )
            )
        ))

        val language by settingsViewModel.language.collectAsState()
        Log.i(TAG, "SetUpNavHost:${language} ")

        val layoutDirection = if (language == Lang.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            NavHost(
                navController = navHostController,
                startDestination = ScreenRoute.HomeScreenRoute,
                modifier = Modifier.fillMaxSize()
            ) {
                composable<ScreenRoute.HomeScreenRoute> {
                    HomeScreen(innerPadding,viewModel(factory = HomeFactory(
                        WeatherRepositoryImpl.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.apiService),
                            WeatherLocalDataSource(WeatherDatabase.getInstance(
                                this@MainActivity).getWeatherDao(),
                                WeatherDatabase.getInstance(this@MainActivity).getHomeWeatherDao(),
                                // WeatherDatabase.getInstance(this@MainActivity).weatherAlertDao(),
                                WeatherDatabase.getInstance(this@MainActivity).alertDao())

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
                            factory = favFactory
                        )
                    )
                }
                composable<ScreenRoute.MapScreenRoute> {
                    MapScreen(
                        navHostController,
                        viewModel(
                            factory = favFactory
                        )
                    )
                }
                composable<ScreenRoute.NotificationScreenRoute> {
                    AlarmScreen(innerPadding, viewModel(factory = AlertFactory(
                        WeatherRepositoryImpl.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.apiService),
                            WeatherLocalDataSource(WeatherDatabase.getInstance(this@MainActivity).getWeatherDao(),
                                WeatherDatabase.getInstance(this@MainActivity).getHomeWeatherDao(),
                                // WeatherDatabase.getInstance(this@MainActivity).weatherAlertDao(),
                                WeatherDatabase.getInstance(this@MainActivity).alertDao()
                            )

                        ), application
                    )
                    ))
                }
                composable<ScreenRoute.SettingScreenRoute> {
                    SettingScreen(
                        innerPadding,
                        viewModel(factory = SettingsFactory(repoForGetWeatherDao)),
                        onNavigateToMap = {
                            navHostController.navigate(ScreenRoute.MapScreenRoute)
                        }
                    )
                }



                composable<ScreenRoute.DetailsFavouriteScreenRoute> {backStackEntry->
                    val lat=backStackEntry.toRoute<ScreenRoute.DetailsFavouriteScreenRoute>().latitude
                    val long=backStackEntry.toRoute<ScreenRoute.DetailsFavouriteScreenRoute>().longitude

                    DetailsFavScreen(viewModel(factory = HomeFactory(
                        WeatherRepositoryImpl.getInstance(
                            WeatherRemoteDataSource(RetrofitHelper.apiService),
                            WeatherLocalDataSource(WeatherDatabase.getInstance(this@MainActivity).getWeatherDao(),
                                WeatherDatabase.getInstance(this@MainActivity).getHomeWeatherDao(),
                                //WeatherDatabase.getInstance(this@MainActivity).weatherAlertDao(),
                                WeatherDatabase.getInstance(this@MainActivity).alertDao())

                        )
                    )),viewModel(factory = LocationViewModelFactory(
                        this@MainActivity
                    )),lat,long)
                }

            }
        }

    }
}






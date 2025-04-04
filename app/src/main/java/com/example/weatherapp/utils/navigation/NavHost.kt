package com.example.weatherapp.utils.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
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
import com.example.weatherapp.home_screen.LocationViewModelFactory
import com.example.weatherapp.setting.SettingScreen
import com.example.weatherapp.setting.SettingsFactory
import com.example.weatherapp.utils.ScreenRoute


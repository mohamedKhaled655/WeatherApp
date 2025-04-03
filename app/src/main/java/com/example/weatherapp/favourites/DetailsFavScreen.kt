package com.example.weatherapp.favourites

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import com.example.weatherapp.data.models.FavoriteLocationEntity
import com.example.weatherapp.home_screen.HomeViewModel
import com.example.weatherapp.home_screen.LocationViewModel

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.weatherapp.R
import com.example.weatherapp.ui.theme.DarkGradientColors
import com.example.weatherapp.ui.theme.LightGradientColors
import com.example.weatherapp.ui.theme.Spacing
import com.example.weatherapp.ui.theme.WeatherTheme
import com.example.weatherapp.utils.Response
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.WeatherResponse
import com.example.weatherapp.home_screen.DailyDetailed
import com.example.weatherapp.home_screen.HourlyForecast
import com.example.weatherapp.home_screen.MainTemperatureDisplay
import com.example.weatherapp.home_screen.WeatherHeader

import com.example.weatherapp.utils.CustomIconImage
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
import com.example.weatherapp.utils.toCelsius
import com.example.weatherapp.utils.toFormattedDate
import com.example.weatherapp.utils.toFormattedDay
import com.example.weatherapp.utils.toFormattedTime
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun DetailsFavScreen(viewModel: HomeViewModel,locationViewModel: LocationViewModel,lat:Double,long:Double)
{

    viewModel.getCurrentWeather(lat, long)
    viewModel.getForecastWeather(lat, long)
    val weatherState = viewModel.currentWeather.collectAsState()
    val forecastWeatherState = viewModel.forecastWeather.collectAsState()
    val messageState = viewModel.message.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundGradient = if (isDarkTheme) DarkGradientColors.backgroundGradient else LightGradientColors.backgroundGradient
    val isRefreshingState by viewModel.isRefreshing.collectAsState()

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->

        Box (
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = backgroundGradient
                    )
                )
                .padding(Spacing.medium)
        ){
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshingState),
                onRefresh={
                    locationViewModel.getFreshLocation()
                    viewModel.refreshWeather(lat, long)

                }
            ){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(Spacing.medium),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        when (val state = weatherState.value) {
                            is Response.Loading -> {
                                CircularProgressIndicator()
                            }
                            is Response.Success -> {
                                WeatherHeader(state.data,TempUnit.Celsius)
                                MainTemperatureDisplay(state.data,TempUnit.Celsius,WindSpeedUnit.MeterPerSec)
                            }
                            is Response.Failure -> {
                                Text(
                                    text = "Error: ${state.error.message}",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    item {
                        when (val state = forecastWeatherState.value) {
                            is Response.Loading -> {
                                CircularProgressIndicator()
                            }
                            is Response.Success -> {
                                HourlyForecast(state.data)
                                Spacer(modifier = Modifier.height(Spacing.large))
                                DailyDetailed(state.data)
                            }
                            is Response.Failure -> {
                                Text(
                                    text = "Error: ${state.error.message}",
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }

        }

    }

}
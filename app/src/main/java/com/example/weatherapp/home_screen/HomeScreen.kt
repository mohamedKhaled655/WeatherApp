package com.example.weatherapp.home_screen

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
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

import com.example.weatherapp.utils.CustomIconImage
import com.example.weatherapp.utils.toCelsius
import com.example.weatherapp.utils.toFormattedDate
import com.example.weatherapp.utils.toFormattedDay
import com.example.weatherapp.utils.toFormattedTime
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun HomeScreen(viewModel: HomeViewModel,locationViewModel: LocationViewModel) {
    //locationViewModel.getFreshLocation()
    LaunchedEffect(Unit) {
        locationViewModel.getFreshLocation()
    }
    val location by remember { locationViewModel.locationState }

    viewModel.getCurrentWeather(location.latitude,location.longitude)
    viewModel.getForecastWeather(location.latitude,location.longitude)
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
                    viewModel.refreshWeather(location.latitude, location.longitude)

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
                                WeatherHeader(state.data)
                                MainTemperatureDisplay(state.data)
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


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherHeader( model:CurrentWeatherModel) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = model.weather.get(0).description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = "✨ Feels like ${model.main.feels_like.toCelsius()}°",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
        }

        CustomIconImage(model.weather[0].icon,100)

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = model.dt.toFormattedTime(model.timezone),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = model.dt.toFormattedDate(model.timezone),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

}

@Composable
fun MainTemperatureDisplay(model:CurrentWeatherModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
               // text = if (temperature > 0) "+$temperature" else "$temperature",
                text = "${model.main.temp.toCelsius()}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = "°C",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier.padding(top = Spacing.medium)
            )
        }

        Text(
            text = model.name,
            color = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.8f),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = Spacing.small)
        )

        CustomIconImage(model.weather[0].icon,250,4)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "🔥 Sunset ${model.sys.sunset.toFormattedTime(model.timezone)}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            Text(
                text = "⛅ Sunrise ${model.sys.sunrise.toFormattedTime(model.timezone)}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(Spacing.medium))

        /*Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        )
                    )
                )
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ){}*/

    }
}


@Composable
fun HourlyForecast(weatherModel: WeatherResponse) {

    Text(
        text = "Three Hourly Details",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)

    )
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(weatherModel.list.size) { index ->
            val data = weatherModel.list[index]
            DailyWeatherItem(
                icon = data.weather.getOrNull(index % data.weather.size)?.icon.orEmpty(),
                hour = data.dt.toFormattedTime(weatherModel.city.timezone),
                temperature = data.main.temp.toCelsius()
            )
        }
    }
}

@Composable
fun DailyWeatherItem(icon:String,hour: String, temperature: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            )
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = hour,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomIconImage(icon,50)

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$temperature°",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
    }
}



@Composable
fun DailyDetailed(weatherModel: WeatherResponse) {
    val cornerRadius = 16.dp
    val dailyWeatherList = weatherModel.list.filter { it.dt_txt.contains("12:00:00") }

    Text(
        text = "Daily Detailed",
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
    ) {
        items(dailyWeatherList.size){ index->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .padding(vertical = 8.dp)
                    .clip(RoundedCornerShape(cornerRadius)),
                shape = RoundedCornerShape(cornerRadius),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.rec),
                        contentDescription = "Weather Background",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = dailyWeatherList[index].dt.toFormattedDay(weatherModel.city.timezone),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "H:${dailyWeatherList[index].main.temp_max.toCelsius()}°   L:${dailyWeatherList[index].main.temp_min.toCelsius()}°",
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = dailyWeatherList[index].dt.toFormattedDate(weatherModel.city.timezone),
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            //verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            CustomIconImage(dailyWeatherList[index].weather[0].icon,90,4)
                            Text(
                                text = dailyWeatherList[index].weather[0].description,
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            Divider()
        }

    }
}






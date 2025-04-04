package com.example.weatherapp.home_screen

import android.widget.Toast
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.weatherapp.data.local.SharedPreferencesHelper
import com.example.weatherapp.data.models.CurrentWeatherModel
import com.example.weatherapp.data.models.WeatherResponse

import com.example.weatherapp.utils.CustomIconImage
import com.example.weatherapp.utils.CustomLottieUrl
import com.example.weatherapp.utils.Lang
import com.example.weatherapp.utils.LocaleHelper
import com.example.weatherapp.utils.TempUnit
import com.example.weatherapp.utils.WindSpeedUnit
import com.example.weatherapp.utils.convertTemperature
import com.example.weatherapp.utils.convertWindSpeed
import com.example.weatherapp.utils.toCelsius
import com.example.weatherapp.utils.toFahrenheit
import com.example.weatherapp.utils.toFormattedDate
import com.example.weatherapp.utils.toFormattedDay
import com.example.weatherapp.utils.toFormattedTime
import com.example.weatherapp.utils.toKelvin
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun HomeScreen(innerPadding: PaddingValues,viewModel: HomeViewModel,locationViewModel: LocationViewModel) {
    //locationViewModel.getFreshLocation()
    LaunchedEffect(Unit) {
        locationViewModel.getFreshLocation()
    }
    val location by remember { locationViewModel.locationState }
    val context= LocalContext.current
    viewModel.getCurrentWeather(context,location.latitude,location.longitude)
    viewModel.getForecastWeather(location.latitude,location.longitude)
    val weatherState = viewModel.currentWeather.collectAsState()
    val forecastWeatherState = viewModel.forecastWeather.collectAsState()
    val message by viewModel.message.collectAsState()
    LaunchedEffect(message) {

        message?.takeIf { it.isNotEmpty() }?.let {
            println("internet:55555555555555555 ${it}")
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }
    val snackBarHostState = remember { SnackbarHostState() }

    val isDarkTheme = isSystemInDarkTheme()
    val backgroundGradient = if (isDarkTheme) DarkGradientColors.backgroundGradient else LightGradientColors.backgroundGradient
    val isRefreshingState by viewModel.isRefreshing.collectAsState()


    viewModel.updateSettings()
    val locationType by viewModel.locationType.collectAsState()
    val tempUnit by viewModel.tempUnit.collectAsState()
    val windSpeedUnit by viewModel.windSpeedUnit.collectAsState()
    val language by viewModel.language.collectAsState()
    val layoutDirection = if (language == Lang.AR) LayoutDirection.Rtl else LayoutDirection.Ltr
    Scaffold(
        modifier = Modifier.padding(


             bottom = innerPadding.calculateBottomPadding()-10.dp
        ),
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = backgroundGradient
                        )
                    )

            ){
                SwipeRefresh(
                    state = rememberSwipeRefreshState(isRefreshingState),
                    onRefresh={
                        locationViewModel.getFreshLocation()
                        viewModel.refreshWeather(context,location.latitude, location.longitude)

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
                                    WeatherHeader(state.data,tempUnit,language)
                                    MainTemperatureDisplay(state.data,tempUnit,windSpeedUnit,language)
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
}


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherHeader( model:CurrentWeatherModel,tempUnit: TempUnit, language: Lang) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(
            modifier = Modifier.weight(2f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = model.weather[0].description,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Spacer(modifier = Modifier.height(Spacing.small))
            Text(
                text = stringResource(
                    R.string.feels_like,
                    convertTemperature(tempUnit, model.main.feels_like),
                    tempUnit.getSymbol(language)
                ),
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
fun MainTemperatureDisplay(model:CurrentWeatherModel,tempUnit: TempUnit,windSpeedUnit:WindSpeedUnit, language: Lang) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.Url("https://lottie.host/a377149e-b277-4906-8c27-7458099ccbe9/rBB1rXg6L2.lottie"))


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp,vertical = Spacing.medium)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
               // text = if (temperature > 0) "+$temperature" else "$temperature",
                text = convertTemperature(tempUnit,model.main.temp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = tempUnit.getSymbol(language),
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
                text = stringResource(
                    R.string.sunset,
                    model.sys.sunset.toFormattedTime(model.timezone)
                ),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.width(Spacing.medium))

            Text(
                text = stringResource(
                    R.string.sunrise,
                    model.sys.sunrise.toFormattedTime(model.timezone)
                ),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(Spacing.medium))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ){
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ){
                    Text(
                        stringResource(R.string.wind),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,

                    )
                    Text("${convertWindSpeed(windSpeedUnit,model.wind.speed)} ${windSpeedUnit.getSymbol(language)}",style = MaterialTheme.typography.bodySmall)
                    CustomLottieUrl("https://lottie.host/a377149e-b277-4906-8c27-7458099ccbe9/rBB1rXg6L2.lottie")
                }

                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        stringResource(R.string.humidity),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,

                    )
                    Text("${model.main.humidity}",style = MaterialTheme.typography.bodySmall)
                    CustomLottieUrl("https://lottie.host/3e29c3b8-653c-46e8-b61a-e64a0bd4f1f1/VrFwzEzKit.lottie")
                }

                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        stringResource(R.string.pressure),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,

                    )
                    Text("${model.main.pressure}",style = MaterialTheme.typography.bodySmall)
                    CustomLottieUrl("https://lottie.host/ddf9fa4c-2fe4-4eca-9717-376355456d3b/wNmWhHqRv5.lottie",)
                }



            }

            HorizontalDivider(Modifier.padding( 8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            )
                        )
                    )
                    .fillMaxWidth()
                    .padding(16.dp)
            ){
                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        "${model.weather[0].main}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,

                    )
                    Text("${model.weather[0].description}",style = MaterialTheme.typography.bodySmall)
                    CustomIconImage(model.weather[0].icon,50)
                }

                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        stringResource(R.string.sun_rise),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,

                    )
                    Text("${model.sys.sunrise.toFormattedTime(model.timezone)}",style = MaterialTheme.typography.bodySmall)
                    CustomLottieUrl("https://lottie.host/dad04c5d-3be4-42a4-9bda-8ae6614fee0b/MX6HTsoYGP.lottie",50)
                }

                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        stringResource(R.string.sun_set),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text("${model.sys.sunset.toFormattedTime(model.timezone)}", style = MaterialTheme.typography.bodySmall)
                    CustomLottieUrl("https://lottie.host/9f824b23-db1d-4a00-8419-b2dd8a23787f/HbktwoOzP6.lottie",50)
                }



            }
        }


    }
}



@Composable
fun HourlyForecast(weatherModel: WeatherResponse) {

    Text(
        text = stringResource(R.string.three_hourly_details),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)

    )
    LazyRow(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
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
            style = MaterialTheme.typography.bodyLarge,


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
        text = stringResource(R.string.daily_detailed),
        color = MaterialTheme.colorScheme.onBackground,
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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






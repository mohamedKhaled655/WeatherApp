package com.example.weatherapp.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Menu
import androidx.compose.ui.unit.dp
import com.example.weatherapp.ui.theme.*
import com.example.weatherapp.ui.theme.Shapes

@Composable
fun WeatherApp() {
    WeatherAppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                       // colors = GradientColors.backgroundGradient
                    )
                )
                .padding(Spacing.medium)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Section
                WeatherHeader(
                    condition = "light snow",
                    feelsLike = -10
                )

                // Main Temperature Display
                MainTemperatureDisplay(
                    temperature = -10,
                    location = "Norway, Nordland"
                )

                // Sunset/Sunrise Info
                SunriseSunsetInfo(
                    sunset = "05:34 PM",
                    sunrise = "09:02 AM"
                )

                // Hourly Forecast
                Text(
                    text = "Hourly Details",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = Spacing.medium)
                )

                HourlyForecast()

                // Daily Details Section Header
                Text(
                    text = "Daily Details",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = Spacing.medium)
                )

                // Daily Details Card
                DailyDetailsCard()

                Spacer(modifier = Modifier.weight(1f))

                // Bottom Navigation
                BottomNavigationBar()
            }
        }
    }
}

@Composable
fun WeatherHeader(condition: String, feelsLike: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.medium)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = condition,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(Spacing.small))
            Text(
                text = "✨ Feels like $feelsLike°",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Today",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Fri, 18 Feb",
                color = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.7f),
                fontSize = FontSizes.body
            )
        }
    }
}

@Composable
fun MainTemperatureDisplay(temperature: Int, location: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.xlarge)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = if (temperature > 0) "+$temperature" else "$temperature",
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
            text = location,
            color = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.8f),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = Spacing.small)
        )
    }
}

@Composable
fun SunriseSunsetInfo(sunset: String, sunrise: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "🔥 Sunset $sunset",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.width(Spacing.medium))

        Text(
            text = "⛅ Sunrise $sunrise",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun HourlyForecast() {
    val hourlyData = remember {
        listOf(
            HourlyData("09PM", -10),
            HourlyData("10PM", -10),
            HourlyData("11PM", -10),
            HourlyData("12AM", -9)
        )
    }
    Column (modifier = Modifier.fillMaxWidth()){
        Text(
            text = "Daily Forecast",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(bottom = 8.dp)
                .align(Alignment.Start)
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            hourlyData.forEach { data ->
                HourlyWeatherItem(hour = data.hour, temperature = data.temperature)
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(hour: String, temperature: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(Shapes.cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = DarkGradientColors.cardGradient
                )
            )
            .padding(vertical = Spacing.medium, horizontal = Spacing.small)
    ) {
        Text(
            text = hour,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = "❄️",
            fontSize = FontSizes.titleLarge
        )

        Spacer(modifier = Modifier.height(Spacing.small))

        Text(
            text = "${temperature}°",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun DailyDetailsCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Shapes.cornerRadius))
            .background(
                Brush.verticalGradient(
                    colors = DarkGradientColors.cardGradient
                )
            )
            .padding(Spacing.medium),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Pressure",
                color = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "991",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }

        Column {
            Text(
                text = "Wind Speed",
                color = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.7f),
                style = MaterialTheme.typography.bodyLarge
            )

            Text(
                text = "1.49",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun BottomNavigationBar() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.medium)
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Home",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )

        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = "Favorites",
            tint = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.5f),
            modifier = Modifier.size(24.dp)
        )

        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications",
            tint = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.5f),
            modifier = Modifier.size(24.dp)
        )

        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = WeatherTheme.getTransparentColor(MaterialTheme.colorScheme.onBackground, 0.5f),
            modifier = Modifier.size(24.dp)
        )
    }
}

data class HourlyData(val hour: String, val temperature: Int)
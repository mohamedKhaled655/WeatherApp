package com.example.weatherapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = White,
    secondary = LightBlue,
    onSecondary = White,
    tertiary = MidnightBlue,
    background = DarkBlue,
    surface = DeepBlue,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = DeepSkyBlue,
    onPrimary = White,
    secondary = DayAccentBlue,
    onSecondary = White,
    tertiary = LightSkyBlue,
    background = SkyBlue,
    surface = PaleSkyBlue,
    onBackground = DarkText,
    onSurface = DarkText
)
object DarkGradientColors {
    val backgroundGradient = listOf(DarkBlue, DeepBlue)
    val cardGradient = listOf(MidnightBlue.copy(alpha = 0.5f), MidnightBlue.copy(alpha = 0.7f))
}


object LightGradientColors {
    val backgroundGradient = listOf(SkyBlue, LightSkyBlue)
    val cardGradient = listOf(PaleSkyBlue.copy(alpha = 0.7f), PaleSkyBlue.copy(alpha = 0.9f))
}
object FontSizes {
    val headerLarge = 80.sp
    val headerMedium = 24.sp
    val titleLarge = 18.sp
    val titleMedium = 16.sp
    val body = 14.sp
}


object Shapes {
    val cornerRadius = 16.dp
    val smallCornerRadius = 8.dp
}


object Spacing {
    val small = 8.dp
    val medium = 16.dp
    val large = 24.dp
    val xlarge = 32.dp
}


@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme


    val gradientColors = if (darkTheme) DarkGradientColors else LightGradientColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}


/*@Composable
fun WeatherAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }



    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,

        content = content

    )
}*/

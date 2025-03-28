package com.example.weatherapp.utils

import com.example.weatherapp.R
import com.example.weatherapp.data.models.FavoriteLocationEntity
import kotlinx.serialization.Serializable

@Serializable
sealed class ScreenRoute(
    val title: String,
    val icon: Int
) {
    @Serializable
    object HomeScreenRoute : ScreenRoute("Home", R.drawable.hom)
    @Serializable
    object FavouriteScreenRoute : ScreenRoute("Favourite", R.drawable.heart)

    @Serializable
    object NotificationScreenRoute : ScreenRoute("Notification", R.drawable.notification)

    @Serializable
    object SettingScreenRoute : ScreenRoute("Setting", R.drawable.setting)

    @Serializable
    object MapScreenRoute : ScreenRoute("Map", R.drawable.baseline_favorite_24)

    @Serializable
    data class DetailsFavouriteScreenRoute(val latitude: Double, val longitude: Double) : ScreenRoute("DetailsFavourite", R.drawable.heart)



}
package com.example.weatherapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast

class NetworkUtils (private val context: Context){

    private var lastKnownStatus: Boolean = isNetworkAvailable(context)

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    fun checkConnectionStatus() {
        val isCurrentlyConnected = isNetworkAvailable(context)

        if (isCurrentlyConnected != lastKnownStatus) {

            lastKnownStatus = isCurrentlyConnected
            if (isCurrentlyConnected) {

                Toast.makeText(context, "Connected to the Internet", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(context, "Disconnected from the Internet", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
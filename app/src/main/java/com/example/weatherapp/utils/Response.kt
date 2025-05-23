package com.example.weatherapp.utils

sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    data class Success<out T>(val data: T) : Response<T>()
    data class Failure(val error: Throwable) : Response<Nothing>()
}
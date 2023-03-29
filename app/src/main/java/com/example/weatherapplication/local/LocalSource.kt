package com.example.weatherapplication.local

import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow

interface LocalSource {

    suspend fun insertWeather(weather: WeatherData)
    suspend fun deleteWeather(weather: WeatherData)
    suspend fun getStoredWeather():List<WeatherData>?
}
package com.example.weatherapplication.remote

import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepositryInterface {

        suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData>
        suspend fun insertWeather(weather: WeatherData)
        suspend fun deleteWeather(weather: WeatherData)
        suspend fun getStoredWeather():List<WeatherData>?
}
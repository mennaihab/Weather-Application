package com.example.weatherapplication.remote

import retrofit2.Response

interface RepositryInterface {

        suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData>

}
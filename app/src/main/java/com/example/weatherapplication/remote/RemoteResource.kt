package com.example.weatherapplication.remote

import retrofit2.Response

interface RemoteResource {

        suspend fun getweatherOverNetwork(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData>
    }

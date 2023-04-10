package com.example.weatherapplication.remote

import retrofit2.Response

class FakeWeatherClient(private var weatherResponse:WeatherData = WeatherData(lat=8.0,lon=88.7)):RemoteResource {


    override suspend fun getweatherOverNetwork(
        lat: Double,
        lon: Double,
        language: String,
        apiKey: String
    ): Response<WeatherData> {
        return Response.success(weatherResponse)
    }
}
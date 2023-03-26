package com.example.weatherapplication.remote

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("onecall")
    suspend fun getWeatherData(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("lang") language: String,
        @Query("appid") apiKey: String
    ): Response<WeatherData>

}
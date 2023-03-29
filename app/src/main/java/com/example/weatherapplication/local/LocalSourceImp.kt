package com.example.weatherapplication.local

import android.content.Context
import com.example.weatherapplication.remote.WeatherData

class LocalSourceImp (context: Context):LocalSource {

    private val weatherDao : WeatherDao by lazy {
        val db  = WeathertDataBase.getInstance(context)
        db.getWeatherDao()

    }

    override suspend fun insertWeather(weather: WeatherData) {
        weatherDao?.insertWeather(weather)
    }

    override suspend fun deleteWeather(weather: WeatherData) {
        weatherDao?.deleteWeather(weather)
    }

    override suspend fun getStoredWeather(): List<WeatherData>? {
        return weatherDao?.getAll()
    }

}
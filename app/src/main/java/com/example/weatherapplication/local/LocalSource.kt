package com.example.weatherapplication.local


import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow

interface LocalSource {

   suspend fun insertWeather(weather: WeatherData)
     fun getStoredWeather(): Flow<WeatherData>?

    fun getAllFavorites(): Flow<List<FavouritesData>>?
    suspend fun insertFavorites(address: FavouritesData)
    suspend fun deleteFavorites(address: FavouritesData)
}
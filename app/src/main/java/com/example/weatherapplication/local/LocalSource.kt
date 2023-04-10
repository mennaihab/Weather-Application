package com.example.weatherapplication.local


import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow

interface LocalSource {


    //home
   suspend fun insertWeather(weather: WeatherData)
     fun getStoredWeather(): Flow<WeatherData>?

     //favourites

    fun getAllFavorites(): Flow<List<FavouritesData>>?
    suspend fun insertFavorites(address: FavouritesData)
    suspend fun deleteFavorites(address: FavouritesData)


    //alerts
    fun getAllAlerts(): Flow<List<AlertsData>>?
    suspend fun insertAlert(alertsData: AlertsData)
    suspend fun deleteAlert(alertsData: AlertsData)

}
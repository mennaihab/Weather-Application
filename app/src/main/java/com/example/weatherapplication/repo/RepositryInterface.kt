package com.example.weatherapplication.repo

import com.example.weatherapplication.models.AlertSettings
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepositryInterface {


        //home
        suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String):  Flow<Response<WeatherData>>
        suspend fun insertWeather(weather: WeatherData)
        fun getStoredWeather():Flow<WeatherData>?

        //Favorites
        fun getAllFavorites(): Flow<List<FavouritesData>>?

        suspend fun insertFavorites(address: FavouritesData)
        suspend fun deleteFavorites(address: FavouritesData)

        //alerts
        fun getAllAlerts(): Flow<List<AlertsData>>?

        suspend fun insertAlert(alertsData: AlertsData)
        suspend fun deleteAlert(alertsData: AlertsData)


        //alertsettings
        fun saveAlertSettings(alertSettings: AlertSettings)
        fun getAlertSettings(): AlertSettings?

        // Shared preferences
        fun putStringInSharedPreferences(key: String, stringInput: String)
        fun getStringFromSharedPreferences(key: String, stringDefault: String): String
        fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean)
        fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean



}
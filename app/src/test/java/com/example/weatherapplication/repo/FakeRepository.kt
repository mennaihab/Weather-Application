package com.example.weatherapplication.repo

import android.content.SharedPreferences
import com.example.weatherapplication.models.AlertSettings
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.*
import retrofit2.Response

class FakeRepository(    private var weatherData: WeatherData? = null,
                         private val favorites: MutableList<FavouritesData> = mutableListOf(),
                         private val alerts: MutableList<AlertsData> = mutableListOf(),
                         private val weatherDataFlow : Flow<Response<WeatherData>> = emptyFlow(),
                  )
    :RepositryInterface {


    override suspend fun allWeatherData(
        lat: Double,
        lon: Double,
        language: String,
        apiKey: String
    ): Flow<Response<WeatherData>> {
        return weatherDataFlow
    }

    override suspend fun insertWeather(weather: WeatherData) {
        weatherData = weather


    }

    override fun getStoredWeather(): Flow<WeatherData>? {
        return flowOf(weatherDataFlow as  WeatherData)
    }

    override fun getAllFavorites(): Flow<List<FavouritesData>>? {
        return flowOf(favorites as List<FavouritesData>)
    }

    override suspend fun insertFavorites(address: FavouritesData) {
  favorites.add(address)
    }

    override suspend fun deleteFavorites(address: FavouritesData) {
       favorites.remove(address)
    }

    override fun getAllAlerts(): Flow<List<AlertsData>>? {
        return return flowOf(alerts as List<AlertsData>)
    }

    override suspend fun insertAlert(alertsData: AlertsData) {
       alerts.add(alertsData)
    }

    override suspend fun deleteAlert(alertsData: AlertsData) {
      alerts.remove(alertsData)
    }

    override fun saveAlertSettings(alertSettings: AlertSettings) {
        TODO("Not yet implemented")
    }

    override fun getAlertSettings(): AlertSettings? {
        TODO("Not yet implemented")
    }

    override fun putStringInSharedPreferences(key: String, stringInput: String) {
        TODO("Not yet implemented")
    }

    override fun getStringFromSharedPreferences(key: String, stringDefault: String): String {
        TODO("Not yet implemented")
    }

    override fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}
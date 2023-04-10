package com.example.weatherapplication.local

import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalSourceImp( private var favouriteList:MutableList<FavouritesData> = mutableListOf<FavouritesData>(),
        private var alertList:MutableList<AlertsData> = mutableListOf<AlertsData>(),
        private var weatherList:MutableList<WeatherData> =mutableListOf< WeatherData>(),
        private var weather:WeatherData =  WeatherData(lat=9.0,lon=86.7)):LocalSource {

    override suspend fun insertWeather(weather: WeatherData) {
        weatherList.add(weather)
    }

    override fun getStoredWeather(): Flow<WeatherData>? = flow{
        emit(weather)
    }

    override fun getAllFavorites(): Flow<List<FavouritesData>>? = flow{
        emit(favouriteList)
    }

    override suspend fun insertFavorites(address: FavouritesData) {
        favouriteList.add(address)
    }

    override suspend fun deleteFavorites(address: FavouritesData) {
       favouriteList.remove(address)
    }

    override fun getAllAlerts(): Flow<List<AlertsData>>? = flow{
        emit(alertList)
    }

    override suspend fun insertAlert(alertsData: AlertsData) {
        alertList.add(alertsData)
    }

    override suspend fun deleteAlert(alertsData: AlertsData) {
        alertList.remove(alertsData)
    }
}
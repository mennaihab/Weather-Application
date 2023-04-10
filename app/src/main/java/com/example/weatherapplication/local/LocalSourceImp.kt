package com.example.weatherapplication.local

import android.content.Context
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow

class LocalSourceImp private constructor(context: Context):LocalSource {


    private val weatherDao : WeatherDao by lazy {
        val db  = WeathertDataBase.getInstance(context)
        db.getWeatherDao()

    }
    private val favouritesDao : FavouritesDao by lazy {
        val db  = WeathertDataBase.getInstance(context)
        db.getFavouritesDao()

    }

    private val alertsDao : AlertsDao by lazy {
        val db  = WeathertDataBase.getInstance(context)
        db.getAlertsDao()

    }

    companion object{
        @Volatile
        private var localDataSourceInstance: LocalSourceImp? = null
        @Synchronized
        fun getInstance(context: Context): LocalSourceImp {
            if(localDataSourceInstance == null){
                localDataSourceInstance = LocalSourceImp(context)
            }
            return localDataSourceInstance!!
        }
    }

    override suspend fun insertWeather(weather: WeatherData) {
        weatherDao?.insertWeather(weather)
    }

    override fun getStoredWeather(): Flow<WeatherData>? {
        return weatherDao?.getAll()
    }

    override fun getAllFavorites(): Flow<List<FavouritesData>>? {
        return favouritesDao.getFavourites()
    }

    override suspend fun insertFavorites(address: FavouritesData) {
       favouritesDao.insertFavourites(address)
    }

    override suspend fun deleteFavorites(address: FavouritesData) {
      favouritesDao.deleteFavourites(address)
    }

    override fun getAllAlerts(): Flow<List<AlertsData>>? {
       return alertsDao.getAlerts()
    }

    override suspend fun insertAlert(alertsData: AlertsData) {
       alertsDao.insertAlert(alertsData)
    }

    override suspend fun deleteAlert(alertsData: AlertsData) {
        alertsDao.deleteAlert(alertsData)
    }


}
package com.example.weatherapplication.local

import android.content.Context
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow

class LocalSourceImp (context: Context):LocalSource {

    private val weatherDao : WeatherDao by lazy {
        val db  = WeathertDataBase.getInstance(context)
        db.getWeatherDao()

    }
    private val favouritesDao : FavouritesDao by lazy {
        val db  = WeathertDataBase.getInstance(context)
        db.getFavouritesDao()

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


}
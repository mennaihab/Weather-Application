package com.example.weatherapplication.remote

import com.example.weatherapplication.models.FavouritesData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepositryInterface {

        suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String):  Flow<Response<WeatherData>>
        suspend fun insertWeather(weather: WeatherData)
        fun getStoredWeather():Flow<WeatherData>?

        //Favorites
        fun getAllFavorites(): Flow<List<FavouritesData>>?

        suspend fun insertFavorites(address: FavouritesData)
        suspend fun deleteFavorites(address: FavouritesData)

        // Shared preferences
        fun putStringInSharedPreferences(key: String, stringInput: String)
        fun getStringFromSharedPreferences(key: String, stringDefault: String): String
        fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean)
        fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean

}
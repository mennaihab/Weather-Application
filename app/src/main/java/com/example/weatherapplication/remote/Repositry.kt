package com.example.weatherapplication.remote

import android.content.SharedPreferences
import com.example.weatherapplication.local.LocalSource
import com.example.weatherapplication.models.FavouritesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class Repositry private constructor(var remoteResource: RemoteResource, var localSource: LocalSource,val sharedPreferences : SharedPreferences):RepositryInterface{

    private val sharedPreferencesEditor = sharedPreferences.edit()
    companion object {
        private var instance : Repositry? = null
        fun getInstance(remoteResource: RemoteResource,localSource: LocalSource,sharedPreferences: SharedPreferences):Repositry{
            return instance?: synchronized(this){
                val temp = Repositry(remoteResource,localSource, sharedPreferences)
                instance = temp
                temp
            }
        }
    }


    override suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String): Flow<Response<WeatherData>> {
        return flow {
            emit(remoteResource.getweatherOverNetwork(lat, lon, language, apiKey))
        }
    }

    override suspend fun insertWeather(weather: WeatherData) {
        localSource.insertWeather(weather)
    }

    override fun getStoredWeather(): Flow<WeatherData>? {
       return  localSource.getStoredWeather()
    }

    override fun getAllFavorites(): Flow<List<FavouritesData>>? {
        return localSource.getAllFavorites()
    }

    override suspend fun insertFavorites(address: FavouritesData) {
        localSource.insertFavorites(address)
    }

    override suspend fun deleteFavorites(address: FavouritesData) {
       localSource.deleteFavorites(address)
    }

    override fun putStringInSharedPreferences(key: String, stringInput: String) {
        sharedPreferencesEditor.putString(key, stringInput)
        sharedPreferencesEditor.apply()
    }

    override fun getStringFromSharedPreferences(key: String, stringDefault: String): String {
        return sharedPreferences.getString(key, stringDefault)!!
    }

    override fun putBooleanInSharedPreferences(key: String, booleanInput: Boolean) {
        sharedPreferencesEditor.putBoolean(key, booleanInput)
        sharedPreferencesEditor.apply()
    }

    override fun getBooleanFromSharedPreferences(key: String, booleanDefault: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, booleanDefault)
    }
    }


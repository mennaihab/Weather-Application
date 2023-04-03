package com.example.weatherapplication.remote

import com.example.weatherapplication.local.LocalSource
import com.example.weatherapplication.models.FavouritesData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class Repositry private constructor(var remoteResource: RemoteResource, var localSource: LocalSource):RepositryInterface{

    companion object {
        private var instance : Repositry? = null
        fun getInstance(remoteResource: RemoteResource,localSource: LocalSource):Repositry{
            return instance?: synchronized(this){
                val temp = Repositry(remoteResource,localSource)
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

   /* override suspend fun deleteWeather(weather: WeatherData) {
        localSource.deleteWeather(weather)
    }

    override suspend fun getStoredWeather(): Flow<List<WeatherData>>?{
        return localSource.getStoredWeather()

    */
    }


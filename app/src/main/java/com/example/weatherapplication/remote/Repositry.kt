package com.example.weatherapplication.remote

import com.example.weatherapplication.local.LocalSource
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

    override suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData> {
        return remoteResource.getweatherOverNetwork(lat, lon, language,apiKey)
    }

    override suspend fun insertWeather(weather: WeatherData) {
        localSource.insertWeather(weather)
    }

    override suspend fun deleteWeather(weather: WeatherData) {
        localSource.deleteWeather(weather)
    }

    override suspend fun getStoredWeather(): List<WeatherData>?{
        return localSource.getStoredWeather()
    }
}
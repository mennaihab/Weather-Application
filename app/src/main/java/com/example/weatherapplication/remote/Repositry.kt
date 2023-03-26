package com.example.weatherapplication.remote

import retrofit2.Response

class Repositry private constructor(var remoteResource: RemoteResource):RepositryInterface{

        companion object {
            private var instance : Repositry? = null
            fun getInstance(remoteResource: RemoteResource):Repositry{
                return instance?: synchronized(this){
                    val temp = Repositry(remoteResource)
                    instance = temp
                    temp
                }
            }
        }


    override suspend fun allWeatherData(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData> {
        return remoteResource.getweatherOverNetwork(lat, lon, language,apiKey)
    }
}
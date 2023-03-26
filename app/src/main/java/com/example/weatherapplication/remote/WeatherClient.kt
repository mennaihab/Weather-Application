package com.example.weatherapplication.remote

import android.util.Log
import retrofit2.Response

class WeatherClient private constructor(): RemoteResource  {

        val apiService : ApiService by lazy {
            RetrofitHelper.getRetrofitInstance().create(ApiService::class.java)
        }

        companion object {
            private var instance : WeatherClient? = null
            fun getInstance(): WeatherClient {
                return instance?: synchronized(this){
                    val temp = WeatherClient()
                    instance = temp
                    temp
                }
            }
        }

    override suspend fun getweatherOverNetwork(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData> {
        var e = apiService.getWeatherData(lat,lon,language,apiKey)

        return apiService.getWeatherData(lat,lon,language,apiKey)
    }

}
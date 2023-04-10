package com.example.weatherapplication.remote

import android.content.Context
import android.util.Log
import retrofit2.Response

open class WeatherClient private constructor(): RemoteResource  {

        val apiService : ApiService by lazy {
            RetrofitHelper.getRetrofitInstance().create(ApiService::class.java)
        }

    companion object {
        @Volatile
        private var remoteDataSourceInstance: WeatherClient? = null

        @Synchronized
        fun getInstance(context: Context): WeatherClient {
            if (remoteDataSourceInstance == null) {
                remoteDataSourceInstance = WeatherClient()
            }
            return remoteDataSourceInstance!!
        }
    }

    override suspend fun getweatherOverNetwork(lat: Double, lon: Double, language: String,apiKey: String): Response<WeatherData> {
        var e = apiService.getWeatherData(lat,lon,language,apiKey)

        return apiService.getWeatherData(lat,lon,language,apiKey)
    }

}
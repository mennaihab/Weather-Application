package com.example.weatherapplication.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


    object RetrofitHelper {
        val baseUrl = "https://api.openweathermap.org/data/2.5/"
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }

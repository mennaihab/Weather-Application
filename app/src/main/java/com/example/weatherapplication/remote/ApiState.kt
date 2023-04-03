package com.example.weatherapplication.remote

import retrofit2.Response

    sealed class ApiState{
        class Success (val data: Response<WeatherData>):ApiState()
        class Failure(val msg:Throwable):ApiState()
        object Loading:ApiState()
    }
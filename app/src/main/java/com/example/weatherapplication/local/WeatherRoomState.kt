package com.example.weatherapplication.local

import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData

sealed class WeatherRoomState {
    class Success (val data:WeatherData?): WeatherRoomState()
    class Failure(val msg:Throwable): WeatherRoomState()
    object Loading: WeatherRoomState()
}
package com.example.weatherapplication.local

import com.example.weatherapplication.models.FavouritesData

sealed class RoomState{
    class Success (val data: List<FavouritesData>?): RoomState()
    class Failure(val msg:Throwable): RoomState()
    object Loading: RoomState()
}
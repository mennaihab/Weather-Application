package com.example.weatherapplication.map

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.repo.RepositryInterface

class MapsViewModel(var _repo: RepositryInterface,) : ViewModel() {

     suspend fun insertFavorite(lat:Double, lon:Double, countryName:String){
        var favouritesData = FavouritesData(countryName,lat,lon)
        Log.i("map","map")
        Log.i("map",favouritesData.address)
        Log.i("map","map")
            _repo.insertFavorites(favouritesData)
        }
    }
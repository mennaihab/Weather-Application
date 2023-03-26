package com.example.weatherapplication.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.remote.Hourly
import com.example.weatherapplication.remote.RepositryInterface
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val _repo : RepositryInterface): ViewModel() {
        private var _weatherData : MutableLiveData<WeatherData> = MutableLiveData<WeatherData>()
        val weatherData : LiveData<WeatherData> = _weatherData

        fun getWeatherData(lat: Double, lon: Double, language: String,apiKey: String){
            viewModelScope.launch(Dispatchers.IO) {
                val response = _repo.allWeatherData(lat,lon,language,apiKey)
                if (response.isSuccessful){
                    withContext(Dispatchers.Main){
                        _weatherData.value = response.body()
                       // response.body()?.current?.weather?.get(0)?.let { Log.i("menna", it.description) }
                    }
                }
            }
        }




        }


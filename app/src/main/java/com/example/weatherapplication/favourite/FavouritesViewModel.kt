package com.example.weatherapplication.favourite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.remote.RepositryInterface
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavouritesViewModel (private val _repo : RepositryInterface): ViewModel() {
    private var _weather : MutableLiveData<List<WeatherData>> = MutableLiveData<List<WeatherData>>()
    val weather : LiveData<List<WeatherData>> = _weather

    init {
        getLocalWeatherData()
    }

    fun deleteProduct(weather: WeatherData){
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteWeather(weather)
            getLocalWeatherData()
        }

    }

    private fun getLocalWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            _weather.postValue(_repo.getStoredWeather())
            }
        }
    }


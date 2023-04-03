package com.example.weatherapplication.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.local.RoomState
import com.example.weatherapplication.local.WeatherRoomState
import com.example.weatherapplication.remote.ApiState
import com.example.weatherapplication.remote.Hourly
import com.example.weatherapplication.remote.RepositryInterface
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val _repo : RepositryInterface): ViewModel() {
    //  private var _weatherData : MutableLiveData<WeatherData> = MutableLiveData<WeatherData>()
    // val weatherData : LiveData<WeatherData> = _weatherData
    var _weatherStateFlow: MutableStateFlow<ApiState> = MutableStateFlow(ApiState.Loading)
    val weatherStateFlow: StateFlow<ApiState> = _weatherStateFlow
    private var _localWeatherStateFlow: MutableStateFlow<WeatherRoomState> = MutableStateFlow(WeatherRoomState.Loading)
    val localWeatherStateFlow: StateFlow<WeatherRoomState> = _localWeatherStateFlow


    fun getWeatherData(lat: Double, lon: Double, language: String, apiKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = _repo.allWeatherData(lat, lon, language, apiKey)
            //  if (response.isSuccessful){
            withContext(Dispatchers.Main) {
                response
                    .catch {
                        _weatherStateFlow.value = ApiState.Failure(it)
                    }
                    .collect {
                        _weatherStateFlow.value = ApiState.Success(it)

                    }
                //  _weatherData.value = response.body()
                //    response.body()?.current?.weather?.get(0)?.let { Log.i("menna", it.description) }
                //     }
            }
        }
    }

    fun insertWeather(weather: WeatherData) {
        viewModelScope.launch {
            _repo.insertWeather(weather)
        }

    }

    fun getStoredWeathers() {
        viewModelScope.launch {
            _repo.getStoredWeather()?.catch { e ->
                _localWeatherStateFlow.value = WeatherRoomState.Failure(e)
            }
                ?.collectLatest {
                    _localWeatherStateFlow.value = WeatherRoomState.Success(it)
                    Log.i("fav view model", "getFavoriteWeathers: " + it)
                }
        }
    }


}
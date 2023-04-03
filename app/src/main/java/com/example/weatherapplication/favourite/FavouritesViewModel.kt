package com.example.weatherapplication.favourite

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.local.RoomState
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.RepositryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavouritesViewModel(private val _repo: RepositryInterface) : ViewModel() {
    private var _favouriteWeather: MutableStateFlow<RoomState> = MutableStateFlow(RoomState.Loading)
    val favouriteWeather: StateFlow<RoomState> = _favouriteWeather




    fun deleteFavourites(fav: FavouritesData) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteFavorites(fav)
            getFavoriteWeathers()
        }

    }

    fun insertFavourites(fav: FavouritesData) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertFavorites(fav)
            getFavoriteWeathers()
        }

    }

    fun getFavoriteWeathers() {
        viewModelScope.launch {
            _repo.getAllFavorites()?.catch { e ->
                _favouriteWeather.value = RoomState.Failure(e)
            }
                ?.collectLatest {

                    _favouriteWeather.value = RoomState.Success(it)
                    Log.i("fav view model", "getFavoriteWeathers: " + it)
                }
        }
    }


}



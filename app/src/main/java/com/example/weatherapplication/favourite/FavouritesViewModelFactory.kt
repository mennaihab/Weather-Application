package com.example.weatherapplication.favourite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.repo.RepositryInterface

class FavouritesViewModelFactory (private val repo : RepositryInterface) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(FavouritesViewModel::class.java)){
            FavouritesViewModel(repo) as T
        }else{
            throw IllegalArgumentException("view model class not found")
        }
    }
}
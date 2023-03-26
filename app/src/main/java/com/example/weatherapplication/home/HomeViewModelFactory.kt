package com.example.weatherapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.remote.RepositryInterface

class HomeViewModelFactory (private val repo : RepositryInterface) : ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
             if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
               return HomeViewModel(repo) as T
            }else{
                throw IllegalArgumentException("view model class not found")
            }
        }
    }

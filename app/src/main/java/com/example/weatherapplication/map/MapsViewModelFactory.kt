package com.example.weatherapplication.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.remote.RepositryInterface


class MapsViewModelFactory(val repository: RepositryInterface) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
                MapsViewModel(repository) as T
            } else {
                throw java.lang.IllegalArgumentException("View modle class not found")
            }
        }
    }



package com.example.weatherapplication.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.remote.RepositryInterface

class SettingsViewModelFactory(val repository: RepositryInterface): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>) : T{
            return if (modelClass.isAssignableFrom(SettingsViewModel::class.java))
            {
                SettingsViewModel(repository) as T
            }
            else{
                throw java.lang.IllegalArgumentException("View model class not found")
            }
        }
    }

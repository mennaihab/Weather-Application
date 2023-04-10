package com.example.weatherapplication.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapplication.repo.Repositry

class AlertsViewModelFactory(var repo: Repositry) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(AlertsViewModel::class.java)) {
                AlertsViewModel(repo) as T
            } else {
                throw java.lang.IllegalArgumentException("View model class not found")
            }
        }
    }

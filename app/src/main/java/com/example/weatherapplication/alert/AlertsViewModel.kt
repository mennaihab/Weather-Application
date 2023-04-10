package com.example.weatherapplication.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapplication.local.AlertsRoamState
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.repo.Repositry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlertsViewModel(val _repo: Repositry):ViewModel(){
     private var _alertsStateFlow:MutableStateFlow<AlertsRoamState> = MutableStateFlow(AlertsRoamState.Loading)
    val alertsStateaFlow =_alertsStateFlow


    fun deleteAlerts(alertsData: AlertsData) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.deleteAlert(alertsData)
           getAlerts()
        }

    }

    fun insertAlert(alertsData: AlertsData) {
        viewModelScope.launch(Dispatchers.IO) {
            _repo.insertAlert(alertsData)
            getAlerts()
        }

    }

    fun getAlerts(){
        viewModelScope.launch {
            _repo.getAllAlerts()?.catch { e ->
                _alertsStateFlow.value = AlertsRoamState.Failure(e)
            }
                ?.collectLatest {

                    _alertsStateFlow.value = AlertsRoamState.Success(it)
                }
        }

    }

}
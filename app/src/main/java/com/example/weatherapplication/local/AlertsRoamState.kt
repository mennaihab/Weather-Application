package com.example.weatherapplication.local

import com.example.weatherapplication.models.AlertsData

sealed class AlertsRoamState {

    class Success (val data: List<AlertsData>?): AlertsRoamState()
    class Failure(val msg:Throwable): AlertsRoamState()
    object Loading: AlertsRoamState()
}
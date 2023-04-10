package com.example.weatherapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "alerts", primaryKeys = ["startTime", "endTime","location"]
)
data class AlertsData(
    var startTime:Long,
    var endTime: Long,
    var location:String,
    var lat:Double,
    var lon:Double,
    ):java.io.Serializable

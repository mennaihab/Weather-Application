package com.example.weatherapplication.remote

import androidx.room.Entity


@Entity(tableName = "Weather", primaryKeys = ["lat", "lon"])
//@TypeConverters(Converter::class)
data class WeatherData(
    val current: Current? =null,
    val daily: List<Daily>? =null,
    val hourly: List<Hourly>? =null,
    val lat: Double,
    val lon: Double,
    val timezone: String? =null,
    val alerts: List<Alert>?=null
):java.io.Serializable

data class Current(
    val clouds: Int,
    val humidity: Int,
    val pressure: Int,
    val dt: Long,
    val temp: Double,
    val wind_speed: Double,
    val weather: List<Weather>
)

data class Daily(
    val dt: Int,
    val temp: Temp,
    val weather: List<Weather>
)

data class Hourly(
    val dt: Int,
    val weather: List<Weather>,
    val temp: Double
)

data class Temp(
    val max: Double,
    val min: Double
)

data class Weather(
    val description: String,
    val icon: String,
    val id: Int
)

data class Alert (
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)

package com.example.weatherapplication.remote

import androidx.room.Entity
import androidx.room.TypeConverters
import com.example.weatherapplication.local.Converter

@Entity(tableName = "Weather", primaryKeys = ["lat", "lon"])
//@TypeConverters(Converter::class)
data class WeatherData(
    val current: Current,
    val daily: List<Daily>,
    val hourly: List<Hourly>,
    val lat: Double,
    val lon: Double,
    val timezone: String?
):java.io.Serializable

data class Current(
    val clouds: Int,
    val humidity: Int,
    val pressure: Int,
    val dt: Int,
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

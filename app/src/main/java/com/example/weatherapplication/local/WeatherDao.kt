package com.example.weatherapplication.local

import androidx.room.*
import com.example.weatherapplication.remote.WeatherData
@Dao
interface WeatherDao {
    @Query("Select * From Weather")
   suspend  fun getAll(): List<WeatherData>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(weather:WeatherData)
    @Delete
    suspend fun deleteWeather(weather: WeatherData): Int
}
package com.example.weatherapplication.local

import androidx.room.*
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("Select * From Weather")
     fun getAll(): Flow<WeatherData>?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWeather(weather:WeatherData)
    @Delete
    suspend fun deleteWeather(weather: WeatherData): Int
}
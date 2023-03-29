package com.example.weatherapplication.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapplication.remote.WeatherData

@Database(entities = arrayOf(WeatherData::class), version = 1 )


    abstract class WeathertDataBase : RoomDatabase() {
        abstract fun getWeatherDao(): WeatherDao
        companion object{
            @Volatile
            private var INSTANCE: WeathertDataBase? = null
            fun getInstance (ctx: Context): WeathertDataBase{
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        ctx.applicationContext, WeathertDataBase::class.java, "weather_database")
                        .build()
                    INSTANCE = instance
                    instance }
            }
        }
    }


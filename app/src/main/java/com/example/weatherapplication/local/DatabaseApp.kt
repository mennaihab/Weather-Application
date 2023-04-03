package com.example.weatherapplication.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.WeatherData

@Database(entities = arrayOf(WeatherData::class, FavouritesData::class), version = 3 )
@TypeConverters(Converter::class)
    abstract class WeathertDataBase : RoomDatabase() {
        abstract fun getWeatherDao(): WeatherDao
         abstract fun getFavouritesDao(): FavouritesDao
        companion object{
            @Volatile
            private var INSTANCE: WeathertDataBase? = null
            fun getInstance (ctx: Context): WeathertDataBase{
                return INSTANCE ?: synchronized(this) {
                    val instance = Room.databaseBuilder(
                        ctx.applicationContext, WeathertDataBase::class.java, "weather_database")
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                    instance }
            }
        }
    }


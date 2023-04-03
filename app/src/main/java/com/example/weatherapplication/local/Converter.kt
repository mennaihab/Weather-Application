package com.example.weatherapplication.local


import androidx.room.TypeConverter
import com.example.weatherapplication.remote.Current
import com.example.weatherapplication.remote.Daily
import com.example.weatherapplication.remote.Hourly
import com.google.gson.Gson

    class Converter{
        @TypeConverter
        fun fromCurrentToGson(current: Current): String = Gson().toJson(current)

        @TypeConverter
        fun fromGsonToCurrent(string: String): Current = Gson().fromJson(string, Current::class.java)

        @TypeConverter
        fun fromDailyListToGson(daily: List<Daily>) = Gson().toJson(daily)!!

        @TypeConverter
        fun fromGsonToDailyList(string: String) =
            Gson().fromJson(string, Array<Daily>::class.java).toList()

        @TypeConverter
        fun fromHourlyListToGson(hourly: List<Hourly>) = Gson().toJson(hourly)!!

        @TypeConverter
        fun fromGsonToHourlyList(stringHourly: String) = Gson().fromJson(stringHourly, Array<Hourly>::class.java).toList()

        /*  @TypeConverter
          fun fromMinutelyToGson(minutely: List<Minutely>): String = Gson().toJson(minutely)

          @TypeConverter
          fun fromGsonToMinutely(string: String): List<Minutely> =
              Gson().fromJson(string, Array<Minutely>::class.java).toList()

          @TypeConverter
          fun fromAlertToGson(alerts: List<Alerts?>?) = Gson().toJson(alerts)!!

          @TypeConverter
          fun fromGsonToAlert(stringAlert: String?) =
              Gson().fromJson(stringAlert, Array<Alerts?>::class.java)?.toList()*/

    }

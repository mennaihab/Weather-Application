package com.example.weatherapplication.local

import androidx.room.*
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import kotlinx.coroutines.flow.Flow


@Dao
interface AlertsDao {

    @Query("SELECT * FROM alerts")
    fun getAlerts(): Flow<List<AlertsData>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alertData: AlertsData)

    @Delete
    suspend fun deleteAlert(alertData: AlertsData)
}
package com.example.weatherapplication.models

import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(
        tableName = "favourites"
    )
    data class FavouritesData (
        @PrimaryKey
        val address: String,
        val lat: Double,
        val lon: Double
    ):java.io.Serializable

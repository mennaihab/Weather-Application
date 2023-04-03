package com.example.weatherapplication.local

import androidx.room.*
import com.example.weatherapplication.models.FavouritesData
import kotlinx.coroutines.flow.Flow
@Dao
interface FavouritesDao {

    @Query("SELECT * FROM favourites")
    fun getFavourites(): Flow<List<FavouritesData>>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavourites(favData: FavouritesData)

   @Delete
    suspend fun deleteFavourites(favData: FavouritesData)
}
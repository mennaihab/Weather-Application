package com.example.weatherapplication.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.weatherapplication.models.FavouritesData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class FavouritesDaoTest {
    @get:Rule
    var instantExecuterRule = InstantTaskExecutorRule()
lateinit var db:WeathertDataBase
lateinit var dao:FavouritesDao
    @Before
    fun intDB() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            WeathertDataBase::class.java).allowMainThreadQueries().build()
        dao=db.getFavouritesDao()

    }

    @After
    fun close() {
        db.close()
    }

    @Test
    fun getFavourites_insertItems_sameCountItems() = runBlocking {
        //given
        val data1 = FavouritesData(
            address = "cairo",
           lat=22.2,
           lon= 23.6
        )

      dao.insertFavourites(data1)

        val data2 = FavouritesData(
            address = "alex",
            lat=23.2,
            lon= 24.6
        )

        dao.insertFavourites(data2)

        val data3 = FavouritesData(
            address = "emirates",
            lat=45.2,
            lon= 38.6
        )

        dao.insertFavourites(data3)
        //when

        val result = dao.getFavourites()?.first()

        //then
        if (result != null) {
            MatcherAssert.assertThat(result.size, Is.`is`(3))
        }

    }

    @Test
    fun insertFavourites_insertOneItem_returnOneItem() = runBlocking {
        //given
        val data = FavouritesData(
            address = "emirates",
            lat=45.2,
            lon= 38.6
        )
        //when
        dao.insertFavourites(data)
        //then
        val result = dao.getFavourites()?.first()
        if (result != null) {
            MatcherAssert.assertThat(result.get(0), IsNull.notNullValue())
        }



    }

    @Test
    fun deleteFavourites_deleteOneItem_returnEmptylist()= runBlocking {
        //given
        val data = FavouritesData(
            address = "emirates",
            lat=45.2,
            lon= 38.6
        )
        dao.insertFavourites(data)

        //when
        dao.deleteFavourites(data)
        //then
        val result = dao.getFavourites()!!.first()
            MatcherAssert.assertThat(result, IsEmptyCollection.empty())


    }
}
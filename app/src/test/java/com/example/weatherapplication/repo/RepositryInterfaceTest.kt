package com.example.weatherapplication.repo

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.local.FakeLocalSourceImp
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.ApiService
import com.example.weatherapplication.remote.FakeWeatherClient
import com.example.weatherapplication.remote.WeatherData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.`is`
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest= Config.NONE)

class RepositryInterfaceTest {
    @get:Rule
    var instantExecuterRule = InstantTaskExecutorRule()
    var api: ApiService? = null
    private lateinit var weatherResponse: WeatherData
    private var favouriteList:MutableList<FavouritesData>?=null
    private lateinit var alertList:MutableList<AlertsData>
    private lateinit var weatherList:MutableList<WeatherData>
    private lateinit var remoteSource:FakeWeatherClient
    private lateinit var localSource:FakeLocalSourceImp
    private lateinit var repositry: Repositry
    private lateinit var sharedPreferences: SharedPreferences
    private var weather:WeatherData =  WeatherData(lat =67.6, lon = 55.8)
    val testingContext: Application = ApplicationProvider.getApplicationContext()
    @Before
    fun setUp() {
        weatherResponse= WeatherData(
        lon=52.2,
        lat=45.0,
        )

        favouriteList = mutableListOf<FavouritesData>(
        FavouritesData("cairo",45.0,52.2) ,
        FavouritesData("alex",55.0,52.2),
        FavouritesData("emirates",46.0,53.2)
        )

        alertList = mutableListOf<AlertsData>(
        AlertsData(111,222,"cairo",45.0,54.2),
        AlertsData(333,444,"alex",45.0,55.2),
        AlertsData(555,666,"emirates",46.0,54.2)

        )

        weatherList=mutableListOf< WeatherData>(
            WeatherData(lat =54.9,lon=9.0),
            WeatherData(lat =56.9,lon=8.0),
            WeatherData(lat =57.9,lon=77.0)
        )
        remoteSource = FakeWeatherClient(weatherResponse)
        localSource = FakeLocalSourceImp(favouriteList!!,alertList,weatherList,weather)
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(testingContext)
        repositry= Repositry(remoteSource,localSource,sharedPreferences)
    }

    @After
    fun tearDown() {
        favouriteList =null
    }

    @Test
    fun allWeatherData_noThing_returnWeatherDetails()= runBlockingTest {
        //given
        //when
        val response = repositry.allWeatherData(
            lat =67.6, lon = 55.8, apiKey="5e0f6f10e9b7bf48be1f3d781c3aa597",
         language = "en"
        ).first()

        //then
        assertThat(response.body() ,`is`(weatherResponse))

    }

    @Test
    fun insertWeather_insertItem_updateSizeOfListByOne()= runBlockingTest {
        //given
        val weather = WeatherData(lat =67.6, lon = 55.8)
        val weather2 = WeatherData(lat =67.6, lon = 55.8)
        //when
        repositry.insertWeather(weather)
        repositry.insertWeather(weather2)

        //then
        assertThat(weatherList.size,`is`(5))

    }

    @Test
    fun getAllFavorites_NoThing_returnTheSAmeSizeOfFavouritesList() = runBlockingTest {
        //given
        //when
       val result = repositry.getAllFavorites()?.first()
        //then
        if (result != null) {
            assertThat(result.size,`is`(favouriteList?.size ))
        }

    }


    @Test
    fun insertFavorites_insertItem_updateSizeOfListByOne()= runBlockingTest {
        //given
        val fav = FavouritesData("tokyo",46.0,52.2)
        //when
        repositry.insertFavorites(fav)
        //then
        assertThat(favouriteList?.size ,`is`(4))

    }

    @Test
    fun deleteFavorites_listItem_returnListSizeDecreasedByOne() = runBlockingTest(){
        //given
        val fav = favouriteList?.get(0)
        //when
        if (fav != null) {
            repositry.deleteFavorites(fav)
        }
        //then
        assertThat(favouriteList?.size ,`is`(2))
    }

    @Test
    fun getAllAlerts_NoThing_returnTheSAmeSizeOfAlertsList()= runBlockingTest {
            //given
            //when
            val result = repositry.getAllAlerts()?.first()
            //then
            if (result != null) {
                assertThat(result.size,`is`(alertList.size))
            }

        }

    @Test
    fun insertAlert_insertItem_updateSizeOfListByOne() = runBlockingTest {
        //given
        val alert =  AlertsData(777,888,"alexandria",45.0,54.2)
        //when
        repositry.insertAlert(alert)
        //then
        assertThat(alertList.size,`is`(4))

    }
    @Test
    fun deleteAlert_listItem_returnListSizeDecreasedByOne()  = runBlockingTest(){
        //given
        val alert = alertList[0]
        //when
        repositry.deleteAlert(alert)
        //then
        assertThat(alertList.size,`is`(2))
    }


}
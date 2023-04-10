package com.example.weatherapplication.home

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.MainCoroutineRule
import com.example.weatherapplication.local.FakeLocalSourceImp
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.FakeWeatherClient
import com.example.weatherapplication.remote.WeatherData
import com.example.weatherapplication.repo.Repositry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.internal.MainDispatcherFactory
import org.hamcrest.core.IsNull
import org.junit.Assert.*
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {

    @get:Rule
    val instance= InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule= MainCoroutineRule()

    private lateinit var remoteDataSource: FakeWeatherClient
    private lateinit var localDataSource: FakeLocalSourceImp
    private lateinit var viewModel: HomeViewModel
    private lateinit var repository: Repositry
    private lateinit var sharedPreferences: SharedPreferences
    private var weatherResponse = WeatherData(
        current = null,
        lat = 31.2000917,
        lon = 29.9187383,
        timezone = "Africa/Cairo",
        hourly = emptyList(),
        daily = emptyList()
    )
    var favoritesList: MutableList<FavouritesData> = mutableListOf<FavouritesData>(

        FavouritesData(
            "Egypt", lat = 41.200917,
            lon = 29.9187383
        ),
        FavouritesData(
            "Italy", lat = 644.2000917,
            lon = 33.9187383
        ), FavouritesData(
            "Germany", lat = 51.2000917,
            lon = 19.9187383
        ), FavouritesData(
            "America", lat = 731.2000917,
            lon = 229.9187383
        )
    )
    val testingContext: Application = ApplicationProvider.getApplicationContext()
    @Before
    fun setUp() {
        remoteDataSource= FakeWeatherClient(weatherResponse)
        localDataSource= FakeLocalSourceImp(favoritesList)
        sharedPreferences =  PreferenceManager.getDefaultSharedPreferences(testingContext)
        repository= Repositry(remoteDataSource,localDataSource, ApplicationProvider.getApplicationContext())
        viewModel= HomeViewModel(repository)
    }

    @After
    fun tearDown() {
    }


    @Test
    fun getWeatherData_weatherModel_notNullResult() {
        //Given arguments of weatherModel
        val lat=31.2000917
        val lon=29.9187383
        val lang="en"
        val validAppid="5e0f6f10e9b7bf48be1f3d781c3aa597"

        //When calling weatherModel Object

        val result= viewModel.getWeatherData(lat,lon,lang,validAppid)
       mainDispatcherRule.resumeDispatcher()
        //Then The result must be not null
        assertThat(result, IsNull.notNullValue())
    }


    @Test
    fun getStoredWeathers() {
        mainDispatcherRule.pauseDispatcher()
        //When retrieving weatherModel Object

        val result=viewModel.getStoredWeathers()
        mainDispatcherRule.resumeDispatcher()
        //Then The object must be inserted

        assertThat(result,IsNull.notNullValue())
    }


    @Test
    fun insertWeather_addCurrentWeatherObjectToDataBase_processSucceeded() {
        mainDispatcherRule.pauseDispatcher()
        //Given arguments of weatherModel
        val lat=33.2917
        val lon=45.9187383


        //When inserting the object in database

        val result=viewModel.insertWeather(WeatherData(lat=lat, lon = lon, timezone = "Egypt"))
        mainDispatcherRule.resumeDispatcher()

        //Then The object must be inserted

        assertThat(result,IsNull.notNullValue())
    }
}

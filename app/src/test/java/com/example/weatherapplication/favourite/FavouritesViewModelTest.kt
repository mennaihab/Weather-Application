package com.example.weatherapplication.favourite

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.MainCoroutineRule
import com.example.weatherapplication.home.HomeViewModel
import com.example.weatherapplication.local.FakeLocalSourceImp
import com.example.weatherapplication.local.RoomState
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.FakeWeatherClient
import com.example.weatherapplication.remote.WeatherData
import com.example.weatherapplication.repo.Repositry
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavouritesViewModelTest {
    @get:Rule
    val instance= InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRule= MainCoroutineRule()

    private lateinit var remoteDataSource: FakeWeatherClient
    private lateinit var localDataSource: FakeLocalSourceImp
    private lateinit var viewModel: FavouritesViewModel
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
        repository= Repositry(remoteDataSource,localDataSource, sharedPreferences)
        viewModel= FavouritesViewModel(repository)
    }

    @After
    fun tearDown() {
    }


    @Test
    fun getFavouriteWeather_nothing_returnFavouritesWeatge()= runBlocking{
        //given
        //when
        viewModel.getFavoriteWeathers()
        var result=viewModel.favouriteWeather.value
        when (result) {
            is RoomState.Loading -> {

            }
            is RoomState.Success -> {

                favoritesList = result.data as MutableList<FavouritesData>
            }
            is RoomState.Failure -> {

            }

        }
        //Then
        assertThat(favoritesList.size, Matchers.`is`(4))
    }

    @Test
    fun insertFavourites_iremFavourites_addListByOne() {
        //pause dispatcher to verify initial value
        mainDispatcherRule.pauseDispatcher()
        //given
       val fav =  FavouritesData(
            "turkey", lat = 41.200917,
            lon = 29.9187383
        )

        //When
        viewModel.insertFavourites(fav)
        viewModel.getFavoriteWeathers()

        var result= viewModel.favouriteWeather.value
        when (result) {
            is RoomState.Loading -> {

            }
            is RoomState.Success -> {

                favoritesList = result.data as MutableList<FavouritesData>
            }
            is RoomState.Failure -> {

            }

        }

        mainDispatcherRule.resumeDispatcher()
        //Then
        assertThat(favoritesList.size, Matchers.`is`(5))
    }


    @Test
    fun deleteFavourites_firstItemOfList_ListSizeDecreazedByOne() {
        mainDispatcherRule.pauseDispatcher()
        //given
        val fav = favoritesList[0]

        //when
        viewModel.deleteFavourites(fav)

        viewModel.getFavoriteWeathers()

        var result= viewModel.favouriteWeather.value
        when (result) {
            is RoomState.Loading -> {

            }
            is RoomState.Success -> {

                favoritesList = result.data as MutableList<FavouritesData>
            }
            is RoomState.Failure -> {

            }

        }

        mainDispatcherRule.resumeDispatcher()
        assertThat(favoritesList.size, Matchers.`is`(3))
        //then

    }
}
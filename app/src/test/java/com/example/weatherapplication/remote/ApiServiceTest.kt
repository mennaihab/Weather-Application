package com.example.weatherapplication.remote

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hamcrest.core.Is
import org.hamcrest.core.IsNull
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.create


@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Config(manifest=Config.NONE)
class ApiServiceTest {
    @get:Rule
    var instantExecuterRule = InstantTaskExecutorRule()
    var api:ApiService? = null

    @Before
    fun openRetrofit() {
        api = RetrofitHelper.getRetrofitInstance().create()
    }

    @After
    fun closeRetrofit() {
        api=null
    }

    @Test
    fun getWeatherData_requestKey_authorized() = runBlocking{
        //given
        val lat = 23.12
        val lon = 5.86
        val apiKey="5e0f6f10e9b7bf48be1f3d781c3aa597"
        val lang = "en"

        //when
        val response = api?.getWeatherData(
            lat = lat,
            lon =lon,
            apiKey =apiKey,
            language = lang
        )

        //then
        assertThat(response?.code()as Int, Is.`is`(200))
        MatcherAssert.assertThat(response.body()!!.lat, Matchers.notNullValue())
    }

    @Test
    fun getWeatherData_requestNoKey_unAuthorized() = runBlocking{
        //Given
        val lon =30.0
        val lat = 25.0
        val lang="en"
        val apiKey = "jjjjjj"

        //When
        val response = api?.getWeatherData(
            lat = lat,
            lon =lon,
            apiKey =apiKey,
            language = lang
        )

        //Then
        MatcherAssert.assertThat(response?.code(), Matchers.`is`(401))
        MatcherAssert.assertThat(response?.body(), IsNull.nullValue())



    }
}
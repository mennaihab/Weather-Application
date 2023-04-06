package com.example.weatherapplication.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapplication.Gps
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.local.Converter
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.local.WeatherRoomState
import com.example.weatherapplication.remote.ApiState
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.remote.WeatherData
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter
    var gps:Gps?=null
    private var _binding: FragmentHomeBinding? = null
    private lateinit var  binding:FragmentHomeBinding
   private lateinit var  pd :ProgressDialog
    lateinit var  repo :Repositry


    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i("sana","on create view")
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding = _binding!!
        val view = binding.root
        repo = Repositry.getInstance(
            WeatherClient.getInstance(requireContext()),
            LocalSourceImp.getInstance(requireContext()),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        homeViewModelFactory = HomeViewModelFactory(repo)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]

        pd = ProgressDialog(context)
            return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("sana","onview created")
        gps = Gps(requireContext(),activity as AppCompatActivity,this)
        val arg: HomeFragmentArgs by navArgs()
        if (arg.isFromFav) {
            Log.i("menna", "from fav")
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
            var favObj = arg.favouriteData
            Log.i("menna", "from fav online")
            if (favObj != null) {
                hitApi(favObj.lat,favObj.lon)
            }
        }
        else if (arg.isFromMap) {
            var lat = arg.latitude.toDouble()
            var lon = arg.longitude.toDouble()
            hitApi(lat,lon)
        }
        //gps
        else if (Utils.isOnline(requireContext())) {
                gps!!.getLastLocation()
                Log.i("menna", "i am gps")

        }

        //room
        else {
            Log.i("menna","i am room")
            Snackbar.make(
                activity?.window?.decorView!!.rootView,
                "Offline",
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(resources.getColor(android.R.color.holo_red_light))
                .show()

            getFromRoom()

        }
    }


        fun hitApi(lat:Double,lon:Double) {
            if(repo.getStringFromSharedPreferences("language","english").equals("english"))
                {
                Log.i("menna","i am english")
                    homeViewModel.getWeatherData(
                        lat,lon,
                        "exclude",
                        "5e0f6f10e9b7bf48be1f3d781c3aa597"
                    )
                }
                else {

                homeViewModel.getWeatherData(
                    lat, lon,
                    "ar",
                    "5e0f6f10e9b7bf48be1f3d781c3aa597"
                )
            }
            lifecycleScope.launch {
                homeViewModel.weatherStateFlow.collectLatest {
                    when (it) {
                        is ApiState.Loading -> {

                            pd.setMessage("loading")
                            pd.show()
                        }
                        is ApiState.Success -> {
                            pd.dismiss()
                            Log.i("menna", "from home")
                            val result = it.data.body()!!
                            homeViewModel.insertWeather(result)
                            fillViews(result)
                        }
                        else -> {
                            pd.dismiss()
                            Toast.makeText(context, "Check your connection", Toast.LENGTH_LONG)
                                .show()
                        }

                    }
                }
            }
        }

        fun fillViews(result: WeatherData) {
            binding.locationText.text = result.timezone
            if (repo.getStringFromSharedPreferences("temperature", "kelvin").equals("celesius")) {
                var temperature = Converter.kelvinToCelsius(result.current.temp)
                binding.temperatureText.text = temperature.toString() + "℃"
            } else if (repo.getStringFromSharedPreferences("temperature", "kelvin")
                    .equals("fahrenheit")
            ) {
                var temperature = Converter.kelvinToFahrenheit(result.current.temp)
                binding.temperatureText.text = temperature.toString() + "F"
            } else {
                binding.temperatureText.text = result.current.temp.toString() + "K"

            }
            binding.descText.text = result.current.weather[0].description
            if (repo.getStringFromSharedPreferences("speed", "meter").equals("meter")) {
                binding.windText.text = result.current.wind_speed.toString() + "mps"
            } else {
                val speed = Converter.meterPerSecondToMilePerHour(result.current.wind_speed)
                binding.windText.text = speed.toString() + "mph"
            }
            binding.humidityText.text = result.current.humidity.toString()
            binding.cloudsText.text = result.current.clouds.toString()
            binding.pressureText.text = result.current.pressure.toString()
            context?.let { it1 ->
                Glide.with(it1)
                    .load("https://openweathermap.org/img/wn/${result.current.weather[0].icon}@2x.png")
                    .into(binding.iconImage)
            }


            hourlyAdapter = HourlyAdapter()
            dailyAdapter = DailyAdapter()

            binding.hourRec.apply {

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                this.adapter = hourlyAdapter
            }
            binding.dayRec.apply {

                layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                this.adapter = dailyAdapter
            }
            hourlyAdapter.submitList(result.hourly.take(24))
            dailyAdapter.submitList(result.daily.take(24))
        }


    fun getFromRoom(){
        homeViewModel.getStoredWeathers()
        lifecycleScope.launch {
            homeViewModel.localWeatherStateFlow.collectLatest {
                when (it) {
                    is WeatherRoomState.Loading -> {

                        pd.setMessage("loading")
                        pd.show()

                    }
                    is WeatherRoomState.Success -> {
                        pd.dismiss()
                        // productAdapter.submitList(it.data.body()?.products)
                        //productAdapter.notifyDataSetChanged()
                        val result = it.data!!
                        binding.locationText.text = result.timezone
                        if (repo.getStringFromSharedPreferences("temperature", "kelvin")
                                .equals("celesius")
                        ) {
                            var temperature =
                                Converter.kelvinToCelsius(result.current.temp)
                            binding.temperatureText.text = temperature.toString() + "℃"
                        } else if (repo.getStringFromSharedPreferences(
                                "temperature",
                                "kelvin"
                            ).equals("fahrenheit")
                        ) {
                            var temperature =
                                Converter.kelvinToFahrenheit(result.current.temp)
                            binding.temperatureText.text = temperature.toString() + "F"
                        } else {
                            binding.temperatureText.text =
                                result.current.temp.toString() + "K"

                        }
                        binding.descText.text = result.current.weather[0].description
                        if (repo.getStringFromSharedPreferences("speed", "meter")
                                .equals("meter")
                        ) {
                            binding.windText.text =
                                result.current.wind_speed.toString() + "mps"
                        } else {
                            val speed =
                                Converter.meterPerSecondToMilePerHour(result.current.wind_speed)
                            binding.windText.text = speed.toString() + "mph"
                        }
                        binding.humidityText.text = result.current.humidity.toString()
                        binding.cloudsText.text = result.current.clouds.toString()
                        binding.pressureText.text = result.current.pressure.toString()
                        context?.let { it1 ->
                            Glide.with(it1)
                                .load("https://openweathermap.org/img/wn/${result.current.weather[0].icon}@2x.png")
                                .into(binding.iconImage)
                        }

                        hourlyAdapter = HourlyAdapter()
                        dailyAdapter = DailyAdapter()

                        binding.hourRec.apply {

                            layoutManager = LinearLayoutManager(
                                context,
                                LinearLayoutManager.HORIZONTAL,
                                false
                            )
                            this.adapter = hourlyAdapter
                        }
                        binding.dayRec.apply {

                            layoutManager = LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL,
                                false
                            )
                            this.adapter = dailyAdapter
                        }
                        hourlyAdapter.submitList(result.hourly.take(24))
                        dailyAdapter.submitList(result.daily.take(24))
                    }
                    else -> {
                        pd.dismiss()
                        Toast.makeText(
                            context,
                            "Check your connection",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }


    }



        override fun onResume() {
            super.onResume()
              //gps.getLastLocation()
            }
            //}


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }

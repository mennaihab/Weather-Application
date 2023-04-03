package com.example.weatherapplication.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.opengl.Visibility
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.AppBarConfiguration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.local.WeatherRoomState
import com.example.weatherapplication.location.PERMISSION_ID
import com.example.weatherapplication.remote.ApiState
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import com.google.android.gms.location.*
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var homeViewModelFactory: HomeViewModelFactory
    private lateinit var hourlyAdapter: HourlyAdapter
    private lateinit var dailyAdapter: DailyAdapter

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        homeViewModelFactory = HomeViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(),
                LocalSourceImp(requireContext())
            )
        )
        val pd = ProgressDialog(context)
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()
        val arg: HomeFragmentArgs by navArgs()
        if(arg.isFromFav)
        {
            Log.i("menna","from fav")
            //var mainActivity = MainActivity()
           // requireActivity().actionBar?.hide()
            (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
            var favObj=arg.favouriteData
            if(Utils.isOnline(requireContext())){
                Log.i("menna","from fav online")
                if (favObj != null) {
                    homeViewModel.getWeatherData(
                        favObj.lat, favObj.lon,
                        "exclude",
                        "5e0f6f10e9b7bf48be1f3d781c3aa597"
                    )

                    lifecycleScope.launch {
                        homeViewModel.weatherStateFlow.collectLatest {
                            when(it){
                                is ApiState.Loading ->{
                                    pd.setMessage("loading")
                                    pd.show()
                                }
                                is ApiState.Success->{
                                    pd.dismiss()
                                    // productAdapter.submitList(it.data.body()?.products)
                                    //productAdapter.notifyDataSetChanged()
                                    val result = it.data.body()!!
                                    homeViewModel.insertWeather(result)
                                    binding.locationText.text = result.timezone
                                    binding.temperatureText.text = result.current.temp.toString()
                                    binding.descText.text = result.current.weather[0].description
                                    binding.windText.text = result.current.wind_speed.toString()
                                    binding.humidityText.text = result.current.humidity.toString()
                                    binding.cloudsText.text = result.current.clouds.toString()
                                    binding.pressureText.text = result.current.pressure.toString()
                                    context?.let { it1 ->
                                        Glide.with(it1)
                                            .load("https://openweathermap.org/img/wn/${result.current.weather[0].icon}@2x.png")
                                            .into(binding.iconImage)
                                    }

                                    //  it.timezone?.let { it1 -> Log.i("hello", it1) }

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
                                else->{
                                    pd.dismiss()
                                    Toast.makeText(context,"Check your connection", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }


                }

            }
            //open fav item without internet
            else{
                Toast.makeText(context,"Check your connection first ", Toast.LENGTH_LONG).show()
            }



        }
        //open home from home and with internet
        else{
            if(Utils.isOnline(requireContext())){
                if (checkPermissions()) {
                    getLastLocation()
                }
                lifecycleScope.launch {
                    homeViewModel.weatherStateFlow.collectLatest {
                        when(it){
                            is ApiState.Loading ->{

                                pd.setMessage("loading")
                                pd.show()

                            }
                            is ApiState.Success->{
                                pd.dismiss()
                                Log.i("menna","from home")
                                // productAdapter.submitList(it.data.body()?.products)
                                //productAdapter.notifyDataSetChanged()
                                val result = it.data.body()!!
                                homeViewModel.insertWeather(result)
                                binding.locationText.text = result.timezone
                                binding.temperatureText.text = result.current.temp.toString()
                                binding.descText.text = result.current.weather[0].description
                                binding.windText.text = result.current.wind_speed.toString()
                                binding.humidityText.text = result.current.humidity.toString()
                                binding.cloudsText.text = result.current.clouds.toString()
                                binding.pressureText.text = result.current.pressure.toString()
                                context?.let { it1 ->
                                    Glide.with(it1)
                                        .load("https://openweathermap.org/img/wn/${result.current.weather[0].icon}@2x.png")
                                        .into(binding.iconImage)
                                }

                                //  it.timezone?.let { it1 -> Log.i("hello", it1) }

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
                            else->{
                                pd.dismiss()
                                Toast.makeText(context,"Check your connection", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }
            //open home from room
            else{
                Snackbar.make(activity?.window?.decorView!!.rootView, "Offline", Snackbar.LENGTH_LONG)
                    .setBackgroundTint(resources.getColor(android.R.color.holo_red_light))
                    .show()
                homeViewModel.getStoredWeathers()
                lifecycleScope.launch {
                    homeViewModel.localWeatherStateFlow.collectLatest {
                        when(it){
                            is WeatherRoomState.Loading ->{

                                pd.setMessage("loading")
                                pd.show()

                            }
                            is WeatherRoomState.Success->{
                                pd.dismiss()
                                // productAdapter.submitList(it.data.body()?.products)
                                //productAdapter.notifyDataSetChanged()
                                val result = it.data!!
                                binding.locationText.text = result.timezone
                                binding.temperatureText.text = result.current.temp.toString()
                                binding.descText.text = result.current.weather[0].description
                                binding.windText.text = result.current.wind_speed.toString()
                                binding.humidityText.text = result.current.humidity.toString()
                                binding.cloudsText.text = result.current.clouds.toString()
                                binding.pressureText.text = result.current.pressure.toString()
                                context?.let { it1 ->
                                    Glide.with(it1)
                                        .load("https://openweathermap.org/img/wn/${result.current.weather[0].icon}@2x.png")
                                        .into(binding.iconImage)
                                }

                                //  it.timezone?.let { it1 -> Log.i("hello", it1) }

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
                            else->{
                                pd.dismiss()
                                Toast.makeText(context,"Check your connection", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            }
        }

        // homeViewModel.getWeatherData("5e0f6f10e9b7bf48be1f3d781c3aa597",23.2,24.7,"en")
        //homeViewModel.getWeatherData(23.2,22.7,"exclude","5e0f6f10e9b7bf48be1f3d781c3aa597")
        //  "4a059725f93489b95183bbcb8c6829b9"

     /*   homeViewModel.weatherData.observe(viewLifecycleOwner) {
            binding.locationText.text = it.timezone
            binding.temperatureText.text = it.current.temp.toString()
            binding.descText.text = it.current.weather[0].description
            binding.windText.text = it.current.wind_speed.toString()
            binding.humidityText.text = it.current.humidity.toString()
            binding.cloudsText.text = it.current.clouds.toString()
            binding.pressureText.text = it.current.pressure.toString()
            context?.let { it1 ->
                Glide.with(it1)
                    .load("https://openweathermap.org/img/wn/${it.current.weather[0].icon}@2x.png")
                    .into(binding.iconImage)
            }

            //  it.timezone?.let { it1 -> Log.i("hello", it1) }

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
            hourlyAdapter.submitList(it.hourly.take(24))
            dailyAdapter.submitList(it.daily.take(24))
        }


      */
        return view
    }


    override fun onResume() {
        super.onResume()
       // if (checkPermissions()) {
         //   getLastLocation()
        //}
    }

    private fun checkPermissions(): Boolean {
        return context?.let {
            ActivityCompat.checkSelfPermission(
                it, android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } == PackageManager.PERMISSION_GRANTED ||
                context?.let {
                    ActivityCompat.checkSelfPermission(
                        it, android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                } == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                    var location: Location? = task.result

                    // if(location==null){
                    requestNewLocationData()
                    Log.i("mennqa", location?.latitude.toString())
                    Log.i("mennqa", location?.longitude.toString())
                   if (location != null) {
                        homeViewModel.getWeatherData(
                            location.latitude, location.longitude,
                            "exclude",
                            "5e0f6f10e9b7bf48be1f3d781c3aa597"
                        )
                    }


                }
            } else {
                // Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    private fun requestNewLocationData(){

        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } != PackageManager.PERMISSION_GRANTED
        )
            println("mmm")
        Looper.myLooper()?.let {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallBack, it
            )
        }

    }

    private val mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            val mLastLocation: Location = locationResult.lastLocation
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

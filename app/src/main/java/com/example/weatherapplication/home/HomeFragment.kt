package com.example.weatherapplication.home

import android.R
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.location.PERMISSION_ID
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.IOException
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
                WeatherClient.getInstance()
            )
        )
        homeViewModel = ViewModelProvider(this, homeViewModelFactory)[HomeViewModel::class.java]
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()


        // homeViewModel.getWeatherData("5e0f6f10e9b7bf48be1f3d781c3aa597",23.2,24.7,"en")
        //homeViewModel.getWeatherData(23.2,22.7,"exclude","5e0f6f10e9b7bf48be1f3d781c3aa597")
        //  "4a059725f93489b95183bbcb8c6829b9"

        homeViewModel.weatherData.observe(viewLifecycleOwner) {
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

        return view
    }




/*
        val items = arrayOf("Gps", "Map")
        val selectedList = ArrayList<Int>()
        val builder = AlertDialog.Builder(context)

        builder.setTitle("choose your preferences")
        builder.setMultiChoiceItems(items, null
        ) { dialog, which, isChecked ->
            if (isChecked) {
                selectedList.add(which)
            } else if (selectedList.contains(which)) {
                selectedList.remove(Integer.valueOf(which))
            }
        }

        builder.setPositiveButton("DONE") { dialogInterface, i ->
            val selectedStrings = ArrayList<String>()

            for (j in selectedList.indices) {
                selectedStrings.add(items[selectedList[j]])
            }

            Toast.makeText(context, "Items selected are: " + Arrays.toString(selectedStrings.toTypedArray()), Toast.LENGTH_SHORT).show()
        }

        builder.show()
*/



    override fun onResume() {
        super.onResume()
        Log.i("mennqa", "habaaal")
        if (checkPermissions()) {
            Log.i("mennqa", "habaaal")
            getLastLocation()
        }
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
            val mGeocoder = context?.let { Geocoder(it, Locale.getDefault()) }
            var addressString = ""

            // Reverse-Geocoding starts
            try {
                val addressList: List<Address> =
                    mGeocoder?.getFromLocation(
                        mLastLocation.latitude,
                        mLastLocation.longitude,
                        1
                    ) as List<Address>

                // use your lat, long value here
                if (addressList.isNotEmpty()) {
                    val address = addressList[0]
                    val sb = StringBuilder()
                    for (i in 0 until address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append("\n")
                    }

                    if (address.premises != null)
                        sb.append(address.premises).append(", ")

                    sb.append(address.subAdminArea).append("\n")
                    sb.append(address.locality).append(", ")
                    sb.append(address.adminArea).append(", ")
                    sb.append(address.countryName).append(", ")
                    sb.append(address.postalCode)


                    addressString = sb.toString()

                }
            } catch (e: IOException) {
                Toast.makeText(context, "Unable connect to Geocoder", Toast.LENGTH_LONG)
                    .show()
            }

            // Finally, the address string is posted in the textView with LatLng.
            //  latTextView.text = mLastLocation.latitude.toString()
            //lonTextView.text = mLastLocation.longitude.toString()
            //textTextView.text = "Address: $addressString"

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

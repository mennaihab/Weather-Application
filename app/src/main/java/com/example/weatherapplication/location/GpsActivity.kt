package com.example.weatherapplication.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.weatherapplication.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

const val PERMISSION_ID = 44
class GpsActivity : AppCompatActivity() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    override fun onResume() {
        super.onResume()
        if (checkPermissions()) {
            getLastLocation()
        }
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this, android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ), PERMISSION_ID
        )

    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    }

    private fun getLastLocation() {

        if (checkPermissions()) {
            if (isLocationEnabled()) {

                requestNewLocationData()
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }

    }

    @SuppressLint("MissingPermission", "SuspiciousIndentation")
    private fun requestNewLocationData() {

        val mLocationRequest = com.google.android.gms.location.LocationRequest()
        mLocationRequest.setPriority(com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest.setInterval(0)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
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
            val mGeocoder = Geocoder(applicationContext, Locale.getDefault())
            var addressString = ""

            // Reverse-Geocoding starts
            try {
                val addressList: List<Address> =
                    mGeocoder.getFromLocation(
                        mLastLocation.latitude,
                        mLastLocation.longitude,
                        1
                    ) as List<Address>

                // use your lat, long value here
                if (addressList != null && addressList.isNotEmpty()) {
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
                Toast.makeText(applicationContext, "Unable connect to Geocoder", Toast.LENGTH_LONG)
                    .show()
            }

            // Finally, the address string is posted in the textView with LatLng.
          //  latTextView.text = mLastLocation.latitude.toString()
            //lonTextView.text = mLastLocation.longitude.toString()
            //textTextView.text = "Address: $addressString"

        }
    }

}
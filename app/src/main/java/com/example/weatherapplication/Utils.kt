package com.example.weatherapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.weatherapplication.alert.AlarmRecieverClass
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                 if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                }
            }
        }
        return false


    }
    fun formatTimeAlert(dt:Long):String{
        val date = Date(dt)
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(date)

    }

    fun formatDateAlert(dt:Long):String{
        val date= Date(dt )
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(date)

    }


    fun formatDate(dt:Long):String{
        val date= Date(dt * 1000L)
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(date)

    }
    fun formatday(dt:Long):String{
        val date= Date(dt * 1000L)
        val sdf = SimpleDateFormat("EEEE")
        return sdf.format(date)

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun canelAlarm(context: Context, alert:String?, requestCode:Int) {
        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent

        alarmMgr = context.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context.applicationContext, AlarmRecieverClass::class.java).putExtra(
            Constants.Alert,alert).let { intent ->
            PendingIntent.getBroadcast(context.applicationContext, requestCode, intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }
        alarmMgr?.cancel(alarmIntent)

    }



    fun getAddressEnglish(context: Context, lat: Double?, lon: Double?):String{

        var address:MutableList<Address>?=null

        val geocoder= Geocoder(context)
        address =geocoder.getFromLocation(lat!!,lon!!,1)
        if (address?.isEmpty()==true)
        {
            return "Unkown location"
        }
        else if (address?.get(0)?.countryName.isNullOrEmpty())
        {
            return "Unkown Country"
        }
        else if (address?.get(0)?.adminArea.isNullOrEmpty())
        {
            return address?.get(0)?.countryName.toString()

        }        else
            return address?.get(0)?.countryName.toString()+" , "+address?.get(0)?.adminArea
    }


    fun isDaily(startTime: Long,endTime:Long):Boolean{
        return endTime-startTime >= 86400000
    }

    fun getIconUrl(iconCode:String):String{
        return  "https://openweathermap.org/img/wn/" + iconCode + "@4x.png";
    }

    fun pickedDateFormatDate(dt: Date): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy")
        return sdf.format(dt)
    }
    fun pickedDateFormatTime(dt:Date): String {
        val sdf = SimpleDateFormat("HH:mm")
        return sdf.format(dt)
    }

    fun getAddressArabic(context: Context,lat:Double,lon:Double):String{
        var address:MutableList<Address>?=null

        val geocoder= Geocoder(context,Locale("ar"))
        address =geocoder.getFromLocation(lat,lon,1)

        if (address?.isEmpty()==true)
        {
            return "Unkown location"
        }
        else if (address?.get(0)?.countryName.isNullOrEmpty())
        {
            return "Unkown Country"
        }
        else if (address?.get(0)?.adminArea.isNullOrEmpty())
        {
            return address?.get(0)?.countryName.toString()

        }
        else
            return address?.get(0)?.countryName.toString()+" , "+address?.get(0)?.adminArea

    }

}

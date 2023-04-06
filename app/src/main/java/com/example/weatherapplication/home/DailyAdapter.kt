package com.example.weatherapplication.home


import android.content.Context
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.ItemDailyBinding
import com.example.weatherapplication.databinding.ItemHourlyBinding
import com.example.weatherapplication.local.Converter
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.remote.Daily
import com.example.weatherapplication.remote.Hourly
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import java.text.SimpleDateFormat
import java.util.*


class DailyAdapter: ListAdapter<Daily, DailyAdapter.ViewHolder>(MyDifUnit()) {

    lateinit var context: Context
    lateinit var binding: ItemDailyBinding
    lateinit var item: List<Hourly>
    lateinit var repo: Repositry


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        repo =   Repositry.getInstance(
            WeatherClient.getInstance(context),
            LocalSourceImp.getInstance(context),
            PreferenceManager.getDefaultSharedPreferences(context)
        )
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDailyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        //holder.binding.hourText.text = format.format(date)
        val date = Date(currentObj.dt * 1000L)
        val format = SimpleDateFormat("EEE", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+2")
        }
        holder.binding.dayNameText.text = format.format(date)

        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${currentObj.weather.get(0).icon}@2x.png")
            .into(holder.binding.dayImage)

        holder.binding.dayDescriptionText.text = currentObj.weather.get(0).description
        holder.binding.dayTempText.text = currentObj.temp.toString()
        var temperatureMin: Double = currentObj.temp.min
        var temperatureMax: Double =  currentObj.temp.max
        if(repo.getStringFromSharedPreferences("temperature","kelvin").equals("celesius"))
        {
            temperatureMin = Converter.kelvinToCelsius( temperatureMin)
            temperatureMax = Converter.kelvinToCelsius( temperatureMax)
            holder.binding.dayTempText.text = temperatureMin.toString()+"/"+temperatureMax.toString()+"℃"
        }
        else if(repo.getStringFromSharedPreferences("temperature","kelvin").equals("fahrenheit")){
            temperatureMin = Converter.kelvinToFahrenheit( temperatureMin)
            temperatureMax = Converter.kelvinToFahrenheit( temperatureMax)
            holder.binding.dayTempText.text = temperatureMin.toString()+"/"+temperatureMax.toString()+"F"
        }
        else{
            holder.binding.dayTempText.text = temperatureMin.toString()+"/"+temperatureMax.toString()+"K"
        }


        }

    class ViewHolder(val binding: ItemDailyBinding) :
        RecyclerView.ViewHolder(binding.root) {}


    class MyDifUnit : DiffUtil.ItemCallback<Daily>() {
        override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
            return oldItem == newItem
        }

    }

}
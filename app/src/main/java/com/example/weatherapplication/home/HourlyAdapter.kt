package com.example.weatherapplication.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.ItemHourlyBinding
import com.example.weatherapplication.remote.Hourly
import java.text.SimpleDateFormat
import java.util.*


class HourlyAdapter: ListAdapter<Hourly, HourlyAdapter.ViewHolder>(MyDifUnit()) {

    lateinit var context: Context
    lateinit var binding: ItemHourlyBinding
    lateinit var item: List<Hourly>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        val date = Date(currentObj.dt * 1000L)
        val format = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+2")
        }
        holder.binding.hourText.text = format.format(date)

        Glide.with(context)
            .load("https://openweathermap.org/img/wn/${currentObj.weather.get(0).icon}@2x.png")
            .into(holder.binding.hourImage)

        holder.binding.tempText.text = currentObj.temp.toString()
    }


    class ViewHolder(val binding: ItemHourlyBinding) :
        RecyclerView.ViewHolder(binding.root) {}


    class MyDifUnit : DiffUtil.ItemCallback<Hourly>() {
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }

    }
}

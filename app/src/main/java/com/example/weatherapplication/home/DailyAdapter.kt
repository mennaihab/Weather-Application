package com.example.weatherapplication.home


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weatherapplication.databinding.ItemDailyBinding
import com.example.weatherapplication.databinding.ItemHourlyBinding
import com.example.weatherapplication.remote.Daily
import com.example.weatherapplication.remote.Hourly
import java.text.SimpleDateFormat
import java.util.*


class DailyAdapter: ListAdapter<Daily, DailyAdapter.ViewHolder>(MyDifUnit()) {

    lateinit var context: Context
    lateinit var binding: ItemDailyBinding
    lateinit var item: List<Hourly>


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
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
        val tempMin = currentObj.temp.min
        val tempMax = currentObj.temp.max
        holder.binding.dayTempText.text = tempMin.toString()+"/"+tempMax.toString()

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
package com.example.weatherapplication.alert

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.databinding.ItemAlertBinding
import com.example.weatherapplication.databinding.ItemFavouriteBinding
import com.example.weatherapplication.remote.WeatherData

class AlertAdapter (private val onClick:(AlertData)->Unit): ListAdapter<AlertData, AlertAdapter.ViewHolder>(MyDifUnit()) {

    lateinit var context: Context
    lateinit var binding: ItemAlertBinding


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentObj = getItem(position)
        holder.binding.startDate.text = currentObj.startDate
        holder.binding.startHour.text = currentObj.startHour
        holder.binding.endDate.text = currentObj.EndDate
        holder.binding.endHour.text = currentObj.EndHour
        holder.binding.alertDeleteButton.setOnClickListener {
            onClick(getItem(position))
        }

    }

    class ViewHolder(val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {}


    class MyDifUnit : DiffUtil.ItemCallback<AlertData>() {
        override fun areItemsTheSame(oldItem: AlertData, newItem: AlertData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: AlertData, newItem: AlertData): Boolean {
            return oldItem == newItem
        }

    }

}
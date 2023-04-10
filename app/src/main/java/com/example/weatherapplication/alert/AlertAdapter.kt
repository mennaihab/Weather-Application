package com.example.weatherapplication.alert

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.ItemAlertBinding
import com.example.weatherapplication.databinding.ItemFavouriteBinding
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.remote.WeatherData

class AlertAdapter (private val onDelete:(AlertsData)->Unit): ListAdapter<AlertsData, AlertAdapter.ViewHolder>(MyDifUnit()) {

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
        holder.binding.startDateText.text = Utils.formatTimeAlert(currentObj?.startTime!!)+"  "+ Utils.formatDateAlert(currentObj.endTime)
        holder.binding.endDateText.text =Utils.formatTimeAlert(currentObj?.endTime!!)+"  "+ Utils.formatDateAlert(currentObj.endTime)
        holder.binding.placeText.text = currentObj.location
        holder.binding.alertDeleteButton.setOnClickListener {
            onDelete(getItem(position))
        }

    }

    class ViewHolder(val binding: ItemAlertBinding) :
        RecyclerView.ViewHolder(binding.root) {}


    class MyDifUnit : DiffUtil.ItemCallback<AlertsData>() {
        override fun areItemsTheSame(oldItem: AlertsData, newItem: AlertsData): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: AlertsData, newItem: AlertsData): Boolean {
            return oldItem == newItem
        }

    }

}
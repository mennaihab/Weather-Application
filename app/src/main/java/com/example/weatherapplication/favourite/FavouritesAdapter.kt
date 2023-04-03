package com.example.weatherapplication.favourite


import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.databinding.ItemFavouriteBinding
import com.example.weatherapplication.models.FavouritesData

private const val TAG = "FavoritesAdatpter"

class FavouritesAdapter(
    var fav: List<FavouritesData>?,
    val onDelete: (FavouritesData) -> Unit,
    val onShowFav: (FavouritesData) -> Unit
) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {
    lateinit var binding: ItemFavouriteBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavouriteBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentObj = fav?.get(position)
        if (currentObj != null) {
            Log.i("adapter",currentObj.address)
        }
        if (currentObj != null) {
            holder.binding.favNameText.text = currentObj.address
            holder.binding.favDeleteButton.setOnClickListener {
                onDelete(currentObj)

            }
            holder.binding.favouriteItem.setOnClickListener {
                onShowFav(currentObj)
            }


        }
    }

    override fun getItemCount(): Int = fav?.size!!
    inner class ViewHolder(var binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root)
}
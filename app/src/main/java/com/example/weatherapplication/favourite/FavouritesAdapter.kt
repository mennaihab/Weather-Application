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
    val onDelete: (FavouritesData) -> Unit,
    val onShowFav: (FavouritesData) -> Unit
) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {

    private val fav: MutableList<FavouritesData> = mutableListOf()
    fun updateList( list:List<FavouritesData>){
        fav.clear()
        fav.addAll(list)
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemFavouriteBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var currentObj = fav.get(position)
        Log.i("adapter",currentObj.address)
        holder.binding.favNameText.text = currentObj.address
        holder.binding.favDeleteButton.setOnClickListener {
            onDelete(currentObj)

        }
        holder.binding.favouriteItem.setOnClickListener {
            onShowFav(currentObj)
        }


    }

    override fun getItemCount(): Int = fav?.size!!
    inner class ViewHolder(var binding: ItemFavouriteBinding) :
        RecyclerView.ViewHolder(binding.root)
}
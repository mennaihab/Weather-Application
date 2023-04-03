package com.example.weatherapplication.favourite

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.NetworkManager
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.FragmentFavouritesBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.local.RoomState
import com.example.weatherapplication.models.FavouritesData
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import kotlinx.coroutines.launch


class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private lateinit var favouriteViewModel: FavouritesViewModel
    private lateinit var favouriteViewModelFactory: FavouritesViewModelFactory
    private lateinit var favouritesAdapter: FavouritesAdapter
    private lateinit var  binding:FragmentFavouritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        binding = _binding!!
        val view = binding.root
        favouriteViewModelFactory = FavouritesViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(),
                LocalSourceImp(requireContext())
            )
        )
        val pd = ProgressDialog(context)
        favouriteViewModel =
            ViewModelProvider(this, favouriteViewModelFactory)[FavouritesViewModel::class.java]
        favouriteViewModel.getFavoriteWeathers()
        if (Utils.isOnline(requireContext())) {
            binding.addFab.visibility = View.VISIBLE
        } else {
            binding.addFab.isEnabled = false
            //binding.addFab.visibility=View.GONE

        }
        binding.addFab.setOnClickListener {
            val action = FavouritesFragmentDirections.actionFavouritesFragmentToMapsFragment().apply {
                isFromFav = true
            }
            Navigation.findNavController(it).navigate(action)
        }


        lifecycleScope.launch {
            favouriteViewModel.favouriteWeather.collect {
                when (it) {
                    is RoomState.Loading -> {
                        pd.setMessage("loading")
                        pd.show()
                    }
                    is RoomState.Failure -> {
                        pd.dismiss()
                        Log.i("menna","fail")
                        Toast.makeText(
                            context,
                            "Can't get data from favourites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is RoomState.Success -> {
                        Log.i("menna","success")
                        pd.dismiss()

                        if (it.data?.isEmpty() == false) {
                            Log.i("data",it.data.get(0).address)
                            binding.favRec.apply {
                                this.adapter = FavouritesAdapter(it.data, {
                                    favouriteViewModel.deleteFavourites(it)
                                }, {
                                    Navigation.findNavController(requireView()).navigate(
                                        FavouritesFragmentDirections.actionFavouritesFragmentToHomeFragment()
                                            .apply {
                                                isFromFav = true
                                                favouriteData = it
                                            }

                                    )
                                })
                                layoutManager = LinearLayoutManager(requireContext())
                                    .apply { orientation = RecyclerView.VERTICAL }
                            }
                        }
                        else{
                            Log.i("menna","ana fady")
                        }

                    }
                }
            }

        }

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }




}
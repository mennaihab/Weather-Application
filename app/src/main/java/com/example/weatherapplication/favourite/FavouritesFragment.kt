package com.example.weatherapplication.favourite

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weatherapplication.R
import com.example.weatherapplication.databinding.FragmentFavouritesBinding
import com.example.weatherapplication.databinding.FragmentHomeBinding
import com.example.weatherapplication.home.HomeViewModel
import com.example.weatherapplication.home.HomeViewModelFactory
import com.example.weatherapplication.home.HourlyAdapter
import com.example.weatherapplication.home.SetUpFragmentDirections
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient


class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var favouriteViewModel: FavouritesViewModel
    private lateinit var favouriteViewModelFactory: FavouritesViewModelFactory
    private lateinit var favouriteAdapter: FavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val view = binding.root
        favouriteViewModelFactory = FavouritesViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(),
                LocalSourceImp(requireContext())
            )
        )
        favouriteViewModel = ViewModelProvider(this, favouriteViewModelFactory)[FavouritesViewModel::class.java]
        favouriteViewModel.weather.observe(viewLifecycleOwner) {
        favouriteAdapter = FavouriteAdapter {  favouriteViewModel.deleteProduct(it) }

        binding.favRec.apply {
            this.adapter = favouriteAdapter
            layoutManager= LinearLayoutManager(context)
        }
            favouriteAdapter.submitList(it)
        }
        binding.addFab.setOnClickListener{
            val action= FavouritesFragmentDirections.actionFavouritesFragmentToMapsFragment()
            findNavController().navigate(action)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
package com.example.weatherapplication.favourite

import android.app.ProgressDialog
import android.os.Bundle
import android.preference.PreferenceManager
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapplication.Utils
import com.example.weatherapplication.databinding.FragmentFavouritesBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.local.RoomState
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.repo.Repositry
import kotlinx.coroutines.launch


class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private lateinit var favouriteViewModel: FavouritesViewModel
    private lateinit var favouriteViewModelFactory: FavouritesViewModelFactory
    private val binding: FragmentFavouritesBinding
        get() = _binding!!

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
        val view = binding.root
        favouriteViewModelFactory = FavouritesViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(requireContext()),
                LocalSourceImp.getInstance(requireContext()),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
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
            val action =
                FavouritesFragmentDirections.actionFavouritesFragmentToMapsFragment().apply {
                    isFromFav = true
                }
            Navigation.findNavController(it).navigate(action)
        }
        binding.favRec.apply {
            this.adapter = FavouritesAdapter({
                favouriteViewModel.deleteFavourites(it)
            }, {
                if (Utils.isOnline(requireContext())) {
                    Navigation.findNavController(requireView()).navigate(
                        FavouritesFragmentDirections.actionFavouritesFragmentToHomeFragment()
                            .apply {
                                isFromFav = true
                                favouriteData = it
                            }

                    )
                } else {
                    Toast.makeText(
                        context,
                        "Check your fav  connection",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            layoutManager = LinearLayoutManager(requireContext())
                .apply { orientation = RecyclerView.VERTICAL }


            viewLifecycleOwner.lifecycleScope.launchWhenCreated {
                favouriteViewModel.favouriteWeather.collect {
                    when (it) {
                        is RoomState.Loading -> {
                            pd.setMessage("loading")
                            pd.show()
                        }
                        is RoomState.Failure -> {
                            pd.dismiss()
                            Log.i("menna", "fail")
                            Toast.makeText(
                                context,
                                "Can't get data from favourites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is RoomState.Success -> {
                            Log.i("menna", "success")
                            pd.dismiss()
                            (binding.favRec.adapter as FavouritesAdapter).updateList(it.data)



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
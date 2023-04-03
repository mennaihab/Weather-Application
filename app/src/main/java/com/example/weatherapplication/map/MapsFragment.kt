package com.example.weatherapplication.map
import com.example.weatherapplication.R
import android.location.Address
import android.location.Geocoder
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weatherapplication.databinding.FragmentMapsBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class MapsFragment : Fragment() {
    companion object {
        const val ZOOM = 16f
    }

    lateinit var binding: FragmentMapsBinding
    lateinit var fusedClient: FusedLocationProviderClient
    lateinit var mapFragment: SupportMapFragment
    lateinit var mMap: GoogleMap
    lateinit var mapsViewModel: MapsViewModel
    lateinit var mapsViewModelFactory: MapsViewModelFactory
    private var address: Address? = null
    private val callback = OnMapReadyCallback { googleMap ->
        mMap = googleMap
        mMap.setOnMapClickListener {
            goToLatLng(it.latitude, it.longitude)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentMapsBinding.inflate(inflater, container, false)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapsViewModelFactory = MapsViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(),
                LocalSourceImp(requireContext())
            )
        )

        mapsViewModel = ViewModelProvider(this, mapsViewModelFactory)[MapsViewModel::class.java]
        mapFragment.getMapAsync(callback)
        mapInitialize()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        val arg: MapsFragmentArgs by navArgs()
        if (arg.isFromFav) {
            binding.saveBtn.visibility = View.VISIBLE
            binding.saveBtn.text = "save data"
            binding.saveBtn.setOnClickListener {
                address?.let { it1 -> Log.i("menna", it1.countryName) }
                address?.let { address ->
                    lifecycleScope.launch {
                        mapsViewModel.insertFavorite(
                            address.latitude,
                            address.longitude, address.countryName
                        )
                        Log.i("menna", "hello")
                        withContext(Dispatchers.Main) {
                            Log.i("map","map")
                            val action = MapsFragmentDirections.actionMapsFragmentToFavouritesFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }

        return binding.root
    }

    private fun mapInitialize() {
        val locationRequest: LocationRequest = LocationRequest()
        locationRequest.setInterval(5000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setSmallestDisplacement(14f)
        locationRequest.setFastestInterval(3000)
        binding.searchEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event.action == KeyEvent.ACTION_DOWN
                || event.action == KeyEvent.KEYCODE_ENTER
            ) {
                if (!binding.searchEditText.text.isNullOrEmpty())
                    goToSearchLocation(binding.searchEditText.text.toString())
            }
            false
        }
        fusedClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun goToLatLng(latitude: Double, longitude: Double) {
        getMapAddress(latitude, longitude)?.let { address ->
            this.address = address
            goToAddress(address)
        }
    }

    private fun goToSearchLocation(query: String) {
        getSearchAddress(query)?.let { address ->
            this.address = address
            address?.let { it1 -> Log.i("mennaihab", it1.countryName) }
            goToAddress(address)
        }
    }

    private fun getMapAddress(latitude: Double, longitude: Double): Address? {
        val geocoder = Geocoder(requireContext()).getFromLocation(latitude, longitude, 1)
        return geocoder?.firstOrNull()
    }

    private fun getSearchAddress(query: String): Address? {
        val geocoder = Geocoder(requireContext()).getFromLocationName(query, 1)
        return geocoder?.firstOrNull()
    }

    private fun goToAddress(address: Address) {
        val latLng = LatLng(address.latitude, address.longitude)
        address?.let { it1 -> Log.i("mennaali", it1.countryName) }
        val update: CameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(latLng))
        mMap.animateCamera(update)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
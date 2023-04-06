package com.example.weatherapplication.settings

import android.content.res.Configuration
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.weatherapplication.R

import com.example.weatherapplication.databinding.FragmentSettingsBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.map.MapsFragmentDirections
import com.example.weatherapplication.remote.Repositry
import com.example.weatherapplication.remote.WeatherClient
import java.util.*


class SettingsFragment : Fragment() {


    private var _binding:FragmentSettingsBinding? = null
     lateinit  var  binding :FragmentSettingsBinding
    private lateinit var settingsViewModelFactory:SettingsViewModelFactory
    private lateinit var settingsViewModel:SettingsViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        binding = _binding!!
        val view = binding.root
        settingsViewModelFactory = SettingsViewModelFactory(
            Repositry.getInstance(
                WeatherClient.getInstance(requireContext()),
                LocalSourceImp.getInstance(requireContext()),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
            )
        )
        settingsViewModel = ViewModelProvider(this, settingsViewModelFactory)[SettingsViewModel::class.java]

        if(settingsViewModel.getStringFromSharedPreferences("language","english").equals("english")){
            binding.languageRadioGroup.check(R.id.english_radio_button)
        }
        else{
            binding.languageRadioGroup.check(R.id.arabic_radio_button)
        }

        if(settingsViewModel.getStringFromSharedPreferences("speed","mile").equals("mile")){
            binding.languageRadioGroup.check(R.id.mile_radio_btn)
        }
        else{
            binding.languageRadioGroup.check(R.id.mete_radio_btn)
        }

        if(settingsViewModel.getStringFromSharedPreferences("temperature","kelvin").equals("celesius")){
            binding.languageRadioGroup.check(R.id.celsius_radio_btn)
        }
        else if(settingsViewModel.getStringFromSharedPreferences("temperature","kelvin").equals("fahrenheit")){
            binding.languageRadioGroup.check(R.id.fahrenheit_radio_btn)
        }
        else{
            binding.languageRadioGroup.check(R.id.kelvin_radio_btn)
        }


        binding.arabicRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("language","arabic")
              //  changeLanguage("arabic")

            }
        }
        binding.englishRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("language","english")
                //changeLanguage("english")

            }
        }
        binding.celsiusRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("temperature","celesius")

            }
        }
        binding.fahrenheitRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("temperature","fahrenheit")

            }
        }
        binding.kelvinRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("temperature","kelvin")

            }
        }
        binding.meteRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("speed","meter")

            }
        }
        binding.mileRadioBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("speed","mile")

            }
        }
        binding.notificationEnabledBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("notification","enabled")

            }
        }
        binding.notificationDisabledBtn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("notification","disabled")

            }
        }
        binding.gpsRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("location","gps")
                //val action = SettingsFragmentDirections.actionSettingsFragmentToMapsFragment()
                //findNavController().navigate(action)

            }
        }
        binding.mapRadioButton.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                settingsViewModel.putStringInSharedPreferences("location","map")
                val action = SettingsFragmentDirections.actionSettingsFragmentToMapsFragment().apply{
                    isFromSettingsOrDialogue = true
                }
                findNavController().navigate(action)
            }
        }
        return view
    }

    private fun changeLanguage(language: String) {

        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context?.resources
        val configuration = Configuration()
        configuration.setLocale(locale)
        resources?.updateConfiguration(configuration, resources.displayMetrics)
        ViewCompat.setLayoutDirection(requireActivity().window.decorView, if (language == "arabic") ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR)
        //activity?.recreate()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
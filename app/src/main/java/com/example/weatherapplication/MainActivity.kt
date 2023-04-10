package com.example.weatherapplication

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherapplication.databinding.ActivityMainBinding
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.repo.Repositry
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = Repositry.getInstance(
            WeatherClient.getInstance(this),
            LocalSourceImp.getInstance(this),
            PreferenceManager.getDefaultSharedPreferences(this)
        )

        if (repo.getStringFromSharedPreferences("language", "english").equals("english")) {
            changeLanguage("en")

        } else {
            changeLanguage("ar")

        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this, navController, binding.drawerLayout)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.alertFragment,
                R.id.favouritesFragment,
                R.id.settingsFragment,
                R.id.homeFragment,
                R.id.mapsFragment
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        NavigationUI.setupWithNavController(binding.navView, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration)
    }

    private fun changeLanguage(language: String) {

        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)
        val resources = this?.resources
        val configuration = Configuration()
        configuration.setLocale(locale)
        resources?.updateConfiguration(configuration, resources.displayMetrics)
        ViewCompat.setLayoutDirection(
            this.window.decorView,
            if (language == "ar") ViewCompat.LAYOUT_DIRECTION_RTL else ViewCompat.LAYOUT_DIRECTION_LTR
        )

    }
}
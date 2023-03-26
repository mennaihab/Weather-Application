package com.example.weatherapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.weatherapplication.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var appBarConfiguration : AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
      NavigationUI.setupActionBarWithNavController(this, navController,binding.drawerLayout)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.alertFragment, R.id.favouritesFragment, R.id.settingsFragment,R.id.homeFragment
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

      NavigationUI.setupWithNavController(binding.navView, navController)
    }
    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.myNavHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration)
    }
}
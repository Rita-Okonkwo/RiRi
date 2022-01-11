package com.tech.riri.androidApp


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            if (destination.id == R.id.onboardingFragment) {
                supportActionBar?.hide()
            } else if ( destination.id == R.id.textListFragment) {
                supportActionBar?.hide()
            } else if ( destination.id == R.id.welcomeFragment) {
                supportActionBar?.show()
            } else if (destination.id == R.id.uploadImageFragment) {
                supportActionBar?.show()
            } else if (destination.id == R.id.pasteLinkFragment) {
                supportActionBar?.show()
            }
        }
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

}

package com.example.runningapp.ui

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.ActivityMainBinding
import com.example.runningapp.others.Constants
import com.example.runningapp.others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var navHostController: NavController

    @Inject
    lateinit var name:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)

        if(name.isNotEmpty()){
            val toolbarText = "Let's go ${name}!!"
            supportActionBar?.title = toolbarText
        }

        if(!isLocationEnabled()) locationBuilder()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navHostController = navHostFragment.findNavController()
        navigateToTrackingFragmentIfNeeded(intent)


        binding.bottomNavigationView.setupWithNavController(navHostController)
        binding.bottomNavigationView.setOnItemReselectedListener(){}


        navHostController.addOnDestinationChangedListener{ _, destination, _ ->
            when(destination.id){
                R.id.runFragment, R.id.settingsFragment, R.id.statisticsFragment -> binding.bottomNavigationView.visibility = View.VISIBLE
                    else -> binding.bottomNavigationView.visibility = View.INVISIBLE
            }
        }


    }

    private fun locationBuilder(){
        val builder = MaterialAlertDialogBuilder(this)
            .setTitle("Location Permission")
            .setMessage("You have to allow location Permission")
            .setIcon(R.drawable.ic_location)
            .setCancelable(false)
            .setPositiveButton("On"){_,_->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("Close"){_,_->
                finish()
            }
            .create()
        builder.show()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }

    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if(intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostController.navigate(R.id.action_global_TrackingFragment)
        }
    }


}
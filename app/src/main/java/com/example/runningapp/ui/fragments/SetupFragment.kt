package com.example.runningapp.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.ActivityMainBinding
import com.example.runningapp.databinding.FragmentSetupBinding
import com.example.runningapp.others.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.others.Constants.KEY_NAME
import com.example.runningapp.others.Constants.KEY_WEIGHT
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment() : Fragment(R.layout.fragment_setup) {

    lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentSetupBinding.bind(view)


        if(!isFirstAppOpen){
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment,true)
                .build()
            findNavController().navigate(R.id.action_setupFragment_to_runFragment,
            savedInstanceState,navOptions)
        }

        binding.btnToRunFragment.setOnClickListener {
            if(writePersonalDataToSharedPref()) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }else{
                Snackbar.make(requireView(),"Please enter all fields",Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref():Boolean{
        val name = binding.tvUserName.text.toString()
        val weight = binding.tvWeight.text.toString()
        if(name.isEmpty() || weight.isEmpty()) return false
        else{
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                .apply()
            val toolbarText = "Let's go ${name}!!"
            (requireActivity() as AppCompatActivity).supportActionBar?.title = toolbarText
            return true
        }
    }





}
package com.example.runningapp.ui.fragments

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.runningapp.R
import com.example.runningapp.adapter.RunAdapter
import com.example.runningapp.databinding.FragmentRunBinding
import com.example.runningapp.others.Constants.REQUEST_CODE_PERMISSION
import com.example.runningapp.others.SortType
import com.example.runningapp.others.TrackingUtility
import com.example.runningapp.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class RunFragment() : Fragment(R.layout.fragment_run) , EasyPermissions.PermissionCallbacks{

    lateinit var binding: FragmentRunBinding
    lateinit var runAdapter: RunAdapter
    lateinit var viewModel:MainViewModel


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRunBinding.bind(view)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        requestPermission()
        setUpRecyclerView()

        when(viewModel.sortType){
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(2)
            SortType.DISTANCE -> binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
        }

        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                when(pos){
                        0 -> viewModel.sortRun(SortType.DATE)
                        1 -> viewModel.sortRun(SortType.AVG_SPEED)
                        2 -> viewModel.sortRun(SortType.DISTANCE)
                        3 -> viewModel.sortRun(SortType.RUNNING_TIME)
                        4 -> viewModel.sortRun(SortType.CALORIES_BURNED)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment2)
        }

    }

    private fun setUpRecyclerView() = binding.rvRuns.apply {
        runAdapter = RunAdapter()
        adapter = runAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }


    private fun requestPermission(){
        if(TrackingUtility.hasLocationPermission(requireContext())){
            return
        }
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.Q){
            EasyPermissions.requestPermissions(this@RunFragment,"You need to Accept Permission to run this App",REQUEST_CODE_PERMISSION,
            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
        }else{
            EasyPermissions.requestPermissions(this@RunFragment,
                "You need to Enable 'Allow all the time' Permission to run this App",REQUEST_CODE_PERMISSION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            EasyPermissions.requestPermissions(this@RunFragment,
                "You need to Enable 'Allow all the time' Permission to run this App",REQUEST_CODE_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
       if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
           AppSettingsDialog.Builder(this).build().show()
       }else{
           requestPermission()
       }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
}
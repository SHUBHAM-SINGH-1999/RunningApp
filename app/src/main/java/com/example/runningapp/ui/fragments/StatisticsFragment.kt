package com.example.runningapp.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentStatisticsBinding
import com.example.runningapp.others.TrackingUtility
import com.example.runningapp.ui.CustomMarkerView
import com.example.runningapp.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round


@AndroidEntryPoint
class StatisticsFragment() : Fragment(R.layout.fragment_statistics) {

    lateinit var viewModel:StatisticsViewModel
    lateinit var binding: FragmentStatisticsBinding
    lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatisticsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        barChart = binding.barChart
        viewModel = ViewModelProvider(this).get(StatisticsViewModel::class.java)
        watchObserver()
        setUpBarChart()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun setUpBarChart(){
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
            setDrawGridLines(false)
        }
        barChart.axisLeft.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
//            setDrawGridLines(false)
        }
        barChart.axisRight.apply {
            axisLineColor = Color.WHITE
            textColor = Color.WHITE
//            setDrawGridLines(false)
        }
        barChart.apply {
            description.text = "Avg Speed Over Time"
            legend.isEnabled = false
        }
    }

    private fun watchObserver(){
        viewModel.totalTime.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTime = TrackingUtility.getFormattedStopWatchedTime(it)
                binding.tvTotalTime.text = totalTime
            }
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it / 1000f
                val distance = round(km * 10f) / 10f
                binding.tvTotalDistance.text = "${distance}km"
            }
        })
        viewModel.totalSpeed.observe(viewLifecycleOwner, Observer {
            it?.let{
              val speed = round(it * 10f) / 10f
                binding.tvAverageSpeed.text = "${speed}km/h"
            }
        })
        viewModel.totalCalories.observe(viewLifecycleOwner, Observer {
            binding.tvTotalCalories.text = "${it}kcal"
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i -> BarEntry(i.toFloat(), it[i].avgSpeedInKMH) }

                val barDataSet = BarDataSet(allAvgSpeeds, "Avg Speed over Time")
                barDataSet.apply {
                    valueTextColor = Color.WHITE
                    color = ContextCompat.getColor(requireContext(), pub.devrel.easypermissions.R.color.colorAccent)
                }
                val lineData = BarData(barDataSet)
                barChart.data = lineData
                val marker = CustomMarkerView(
                    it.reversed(),
                    requireContext(),
                    R.layout.marker_view
                )
                barChart.marker = marker
                barChart.invalidate()
            }
        })
    }
}
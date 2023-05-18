package com.example.runningapp.viewmodels

import androidx.lifecycle.ViewModel
import com.example.runningapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor (private val repository: MainRepository) :ViewModel() {

    val totalTime = repository.getTotalTime()
    val totalDistance = repository.getTotalDistance()
    val totalSpeed = repository.getTotalAvgSpeed()
    val totalCalories = repository.getTotalCalories()

    val runsSortedByDate = repository.getAllRunSortedByDate()
}
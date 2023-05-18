package com.example.runningapp.viewmodels

import android.util.Log
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.runningapp.db.Run
import com.example.runningapp.others.SortType
import com.example.runningapp.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (private var repository: MainRepository) :ViewModel() {

    private val runSortedByDate = repository.getAllRunSortedByDate()
    private val runSortedBySpeed = repository.getAllRunSortedBySpeed()
    private val runSortedByDistance = repository.getAllRunSortedByDistanceInMeter()
    private val runSortedByTime = repository.getAllRunSortedByTimeInMillis()
    private val runSortedByCalories = repository.getAllRunSortedByCaloriesBurnt()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init{
        runs.addSource(runSortedByDate){ result ->
            if(sortType==SortType.DATE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedBySpeed){ result ->
            if(sortType==SortType.AVG_SPEED){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByDistance){ result ->
            if(sortType==SortType.DISTANCE){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByTime){ result ->
            if(sortType==SortType.RUNNING_TIME){
                result?.let { runs.value = it }
            }
        }
        runs.addSource(runSortedByCalories){ result ->
            if(sortType==SortType.CALORIES_BURNED){
                result?.let { runs.value = it }
            }
        }
    }

    fun sortRun(sortType: SortType) = when(sortType){
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTime.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedBySpeed.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCalories.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }

    fun insertRun(run: Run) = viewModelScope.launch {
        repository.insertRun(run)
    }

}
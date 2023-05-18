package com.example.runningapp.repositories

import androidx.lifecycle.LiveData
import androidx.room.Query
import com.example.runningapp.db.Run
import com.example.runningapp.db.RunDAO
import javax.inject.Inject

class MainRepository @Inject constructor(private var runDAO: RunDAO) {

    suspend fun insertRun(run: Run) = runDAO.insertRun(run)
    suspend fun deleteRun(run:Run) = runDAO.deleteRun(run)

    fun getAllRunSortedBySpeed() = runDAO.getALlRunSortedByAvgSpeed()
    fun getAllRunSortedByDistanceInMeter() = runDAO.getAllRunSortedByDistanceInMeter()
    fun getAllRunSortedByDate() = runDAO.getAllRunSortedByDate()
    fun getAllRunSortedByTimeInMillis() = runDAO.getAllRunSortedByTimeInMillis()
    fun getAllRunSortedByCaloriesBurnt() = runDAO.getAllRunSortedByCaloriesBurnt()

    fun getTotalAvgSpeed() = runDAO.getTotalAvgSpeed()
    fun getTotalDistance() =runDAO.getTotalDistance()
    fun getTotalTime()= runDAO.getTotalTime()
    fun getTotalCalories() = runDAO.getTotalCalories()

}
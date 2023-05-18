package com.example.runningapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.example.runningapp.R
import com.example.runningapp.databinding.FragmentStatisticsBinding
import com.example.runningapp.databinding.MarkerViewBinding
import com.example.runningapp.db.Run
import com.example.runningapp.others.TrackingUtility
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@SuppressLint("ViewConstructor")
class CustomMarkerView(val runs: List<Run>, c: Context, layoutId: Int) : MarkerView(c,layoutId) {

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if(e == null) {
            return
        }
        val curRunId = e.x.toInt()
        val run = runs[curRunId]
        val calendar = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            findViewById<TextView>(R.id.tvDate).text = dateFormat.format(calendar.time)

        "${run.avgSpeedInKMH}km/h".also {
            findViewById<TextView>(R.id.tvAvgSpeed).text = it
        }
        "${run.distanceInMeter / 1000f}km".also {
            findViewById<TextView>(R.id.tvDistance).text = it
        }
        findViewById<TextView>(R.id.tvDuration).text = TrackingUtility.getFormattedStopWatchedTime(run.timeInMills)

        "${run.caloriesBurnt}kcal".also {
            findViewById<TextView>(R.id.tvCaloriesBurned).text = it
        }
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

}
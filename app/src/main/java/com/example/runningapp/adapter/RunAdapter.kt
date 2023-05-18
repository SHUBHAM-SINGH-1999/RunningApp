package com.example.runningapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.runningapp.R
import com.example.runningapp.db.Run
import com.example.runningapp.others.TrackingUtility
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RunAdapter: RecyclerView.Adapter<RunAdapter.viewholder>() {
    inner class viewholder(itemView: View) : ViewHolder(itemView)

    val differCallback = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this,differCallback)

    fun submitList(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val inf = LayoutInflater.from(parent.context).inflate(R.layout.item_run,parent,false)
        return viewholder(inf)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            // for image
            Glide.with(this).load(run.img).into(findViewById<ImageView>(R.id.ivRunImage))
            //for date
            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            findViewById<TextView>(R.id.tvDate).text = dateFormat.format(calendar.time)

            //for AverageSpeed
            findViewById<TextView>(R.id.tvAvgSpeed).text = "${run.avgSpeedInKMH}km/h"
            //for distance
            findViewById<TextView>(R.id.tvDistance).text = "${run.distanceInMeter/1000f}km"
            //for time
            findViewById<TextView>(R.id.tvTime).text = TrackingUtility.getFormattedStopWatchedTime(run.timeInMills,false)
            //for CaloriesBurnt
            findViewById<TextView>(R.id.tvCalories).text = "${run.caloriesBurnt}kcal"

        }
    }
}
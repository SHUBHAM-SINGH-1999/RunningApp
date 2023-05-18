package com.example.runningapp.others

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Build
import com.example.runningapp.services.Polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit

object TrackingUtility {

    fun hasLocationPermission(context: Context) =
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q){
            EasyPermissions.hasPermissions(context,
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
        }else{
            EasyPermissions.hasPermissions(context,
                Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }


    fun getFormattedStopWatchedTime(ms:Long,includeMillis: Boolean= false):String{
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val min = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(min)
        val sec = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        if(!includeMillis){
            return "${if(hours<10) "0" else ""}$hours:" +
                    "${if(min < 10) "0" else ""}$min:" +
                    "${if(sec<10) "0" else ""}$sec"
        }
        milliseconds -=TimeUnit.SECONDS.toMillis(sec)
        milliseconds /= 10
        return "${if(hours<10) "0" else ""}$hours:" +
                "${if(min < 10) "0" else ""}$min:" +
                "${if(sec<10) "0" else ""}$sec:" +
                "${if(milliseconds< 10) "0" else ""}$milliseconds "
    }


    fun calculatePolyLineLength(polyline: Polyline):Float{
        var distance = 0f
        for(i in 0..polyline.size - 2){
            val pos1 = polyline[i]
            val pos2 = polyline[i+1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,pos1.longitude,
                pos2.latitude,pos2.longitude,result
            )
            distance += result[0]
        }
        return distance
    }
}
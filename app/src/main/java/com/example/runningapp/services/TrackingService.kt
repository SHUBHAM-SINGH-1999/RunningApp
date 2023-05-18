package com.example.runningapp.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationRequest
import android.os.Build
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.example.runningapp.R
import com.example.runningapp.others.Constants.ACTION_PAUSE_SERVICE
import com.example.runningapp.others.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.example.runningapp.others.Constants.ACTION_START_OR_RESUME_SERVICE
import com.example.runningapp.others.Constants.ACTION_STOP_SERVICE
import com.example.runningapp.others.Constants.FASTEST_LOCATION_INTERVAL
import com.example.runningapp.others.Constants.LOCATION_UPDATE_INTERVAL
import com.example.runningapp.others.Constants.NOTIFICATION_CHANNEL_ID
import com.example.runningapp.others.Constants.NOTIFICATION_CHANNEL_NAME
import com.example.runningapp.others.Constants.NOTIFICATION_ID
import com.example.runningapp.others.Constants.TIME_UPDATE_INTERVAL
import com.example.runningapp.others.TrackingUtility
import com.example.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias Polyline = MutableList<LatLng>
typealias PolyLines = MutableList<Polyline>

@AndroidEntryPoint
class TrackingService :LifecycleService() {

    private var isFirstRun = true
    private var isServiceKilled = false

    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @Inject
    lateinit var baseNotificationBuilder: NotificationCompat.Builder

    lateinit var curNotificationBuilder: NotificationCompat.Builder

    private val timeRunInSec = MutableLiveData<Long>()

    companion object{
        val timeRunInMills = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<PolyLines>()
    }

    private fun postInitialValue(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
        timeRunInSec.postValue(0L)
        timeRunInMills.postValue(0L)
    }

    override fun onCreate() {
        super.onCreate()
        curNotificationBuilder = baseNotificationBuilder
        postInitialValue()
//        fusedLocationProviderClient = FusedLocationProviderClient(this)

        isTracking.observe(this, Observer {
            updateLocationTracking(it)
            updateNotificationTrackingState(it)
        })

    }

    private fun pauseService(){
        isTracking.postValue(false)
        isTimerEnabled=false
    }

    private fun updateNotificationTrackingState(isTracking: Boolean){
        val notificationActionText = if(isTracking) "Pause" else "Resume"
        val pendingIntent = if(isTracking){
            val pauseIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_PAUSE_SERVICE
            }
            PendingIntent.getService(this,1,pauseIntent,PendingIntent.FLAG_MUTABLE)
        }else{
            val startIntent = Intent(this,TrackingService::class.java).apply {
                action = ACTION_START_OR_RESUME_SERVICE
            }
            PendingIntent.getService(this,1,startIntent,PendingIntent.FLAG_MUTABLE)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        curNotificationBuilder.javaClass.getDeclaredField("mActions").apply {
            isAccessible = true
            set(curNotificationBuilder,ArrayList<NotificationCompat.Action>())
        }

        if(!isServiceKilled){
            curNotificationBuilder = baseNotificationBuilder
                .addAction(R.drawable.pause,notificationActionText,pendingIntent)
            notificationManager.notify(NOTIFICATION_ID,curNotificationBuilder.build())
        }

    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking:Boolean){
        if(isTracking){
            if(TrackingUtility.hasLocationPermission(this)){
                val request = com.google.android.gms.location.LocationRequest().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(request,locationCallBack(),
                    Looper.getMainLooper())
            }
        }else{
            fusedLocationProviderClient.removeLocationUpdates(locationCallBack())
        }
    }

    private fun locationCallBack() = object :LocationCallback(){
        override fun onLocationResult(result: LocationResult?) {
            if(isTracking.value!!){
                result?.locations?.let {
                    for(location in it){
                        addPathPoints(location)
                        //Log.d("shu",location.latitude.toString()+" , "+location.longitude.toString())
                    }
                }
            }
            super.onLocationResult(result)
        }
    }

    private fun addPathPoints(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude,location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun killService(){
        isServiceKilled = true
        isFirstRun = true
        pauseService()
        postInitialValue()
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action){
                ACTION_START_OR_RESUME_SERVICE -> {
                    if(isFirstRun){
                        startForegroundService()
                        isFirstRun = false
                    }else{
                        startTimer()
//                        Log.d("shu","Resuming Service")
                    }
                }ACTION_PAUSE_SERVICE->{
                    pauseService()
//                    Log.d("shu","Pause Service")
                }ACTION_STOP_SERVICE->{
                    killService()
//                     Log.d("shu","Stop Service")
                }
                else -> {
//                    Log.d("shu","else")
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private var isTimerEnabled = false
    private var timeRun = 0L
    private var lapTime = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private fun startTimer(){
        addEmptyPolyline()
        isTracking.postValue(true)
        timeStarted = System.currentTimeMillis()
        isTimerEnabled = true
        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!){
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMills.postValue(lapTime + timeRun)
                if(timeRunInMills.value!! >= lastSecondTimestamp + 1000L){
                    timeRunInSec.postValue(timeRunInSec.value!! + 1)
                    lastSecondTimestamp += 1000L
                }
                delay(TIME_UPDATE_INTERVAL)
            }
            timeRun += lapTime
        }
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        startForeground(NOTIFICATION_ID,baseNotificationBuilder.build())

        timeRunInSec.observe(this, Observer {
            if(!isServiceKilled){
                val notification = curNotificationBuilder
                    .setContentText(TrackingUtility.getFormattedStopWatchedTime(it*1000L))
                notificationManager.notify(NOTIFICATION_ID,notification.build())
            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }

}
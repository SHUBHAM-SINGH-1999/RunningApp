package com.example.runningapp.di

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.runningapp.R
import com.example.runningapp.others.Constants
import com.example.runningapp.ui.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @ServiceScoped
    @Provides
    fun providesMainActivityPendingIntent(@ApplicationContext app: Context) = PendingIntent.getActivity(
        app,
        0,
        Intent(app, MainActivity::class.java).also {
            it.action = Constants.ACTION_SHOW_TRACKING_FRAGMENT
        },
        PendingIntent.FLAG_MUTABLE
    )

    @ServiceScoped
    @Provides
    fun providesMainActivityNotificationBuilder(@ApplicationContext app: Context,pendingIntent: PendingIntent) = NotificationCompat.Builder(app, Constants.NOTIFICATION_CHANNEL_ID)
        .setAutoCancel(false)
        .setOngoing(true)
        .setContentTitle("Running App")
        .setContentText("00:00:00")
        .setSmallIcon(R.drawable.ic_run)
        .setContentIntent(pendingIntent)

    @ServiceScoped
    @Provides
    fun providesFusedLocationProviderClient(@ApplicationContext app: Context) = FusedLocationProviderClient(app)

}
package com.example.runningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.example.runningapp.db.RunningDatabase
import com.example.runningapp.others.Constants.KEY_FIRST_TIME_TOGGLE
import com.example.runningapp.others.Constants.KEY_NAME
import com.example.runningapp.others.Constants.KEY_WEIGHT
import com.example.runningapp.others.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {

    @Singleton
    @Provides
    fun providesRunningDatabase(@ApplicationContext context:Context): RunningDatabase {
        return Room.databaseBuilder(context,RunningDatabase::class.java,"Room").build()
    }

    @Singleton
    @Provides
    fun providesRunDAO(runningDatabase: RunningDatabase) = runningDatabase.getRunDAO()


    @Singleton
    @Provides
    fun providesSharedPreferences(@ApplicationContext app: Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun providesName(sharedPreferences: SharedPreferences) =
        sharedPreferences.getString(KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun providesWeight(sharedPreferences: SharedPreferences) =
        sharedPreferences.getFloat(KEY_WEIGHT, 80f)

    @Singleton
    @Provides
    fun providesFirstTimeToggle(sharedPreferences: SharedPreferences) =
        sharedPreferences.getBoolean(KEY_FIRST_TIME_TOGGLE,true)

}
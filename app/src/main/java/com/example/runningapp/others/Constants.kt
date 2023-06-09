package com.example.runningapp.others

import android.graphics.Color

object Constants {
   const val REQUEST_CODE_PERMISSION = 999


   const val NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID"
   const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
   const val NOTIFICATION_ID = 1


   const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

   const val LOCATION_UPDATE_INTERVAL = 5000L
   const val FASTEST_LOCATION_INTERVAL = 2000L

   const val POLYLINE_COLOR = Color.RED
   const val POLYLINE_WIDTH = 8f
   const val MAP_ZOOM = 16f

   const val TIME_UPDATE_INTERVAL =50L

   const val CANCEL_TRACKING_DIALOG = "cancelTrackingDialog"

   const val SHARED_PREFERENCES_NAME = "sharedPref"
   const val KEY_FIRST_TIME_TOGGLE = "KEY_FIRST_TIME_TOGGLE"
   const val KEY_NAME = "KEY_NAME"
   const val KEY_WEIGHT = "KEY_WEIGHT"


   const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
   const val ACTION_PAUSE_SERVICE =  "ACTION_PAUSE_SERVICE"
   const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"

}
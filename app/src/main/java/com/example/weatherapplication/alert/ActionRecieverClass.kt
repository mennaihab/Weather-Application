package com.example.weatherapplication.alert

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weatherapplication.Constants

class ActionRecieverClass: BroadcastReceiver() {
    var r= AlarmRecieverClass.r
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onReceive(context: Context, intent: Intent) {
        val notification= NotificationHelper(context)
        if (intent.action.equals(Constants.ACTION_SNOOZE)) {
            r.stop()
            notification.alarmNotificationManager(context).cancel(1)
        }
    }
}
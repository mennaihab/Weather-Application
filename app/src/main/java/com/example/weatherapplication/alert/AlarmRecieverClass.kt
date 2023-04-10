package com.example.weatherapplication.alert

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.work.WorkManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.weatherapplication.Constants
import com.example.weatherapplication.Utils
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.repo.Repositry
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AlarmRecieverClass : BroadcastReceiver() {
    lateinit var notificationManager: NotificationManager
    var notificationId: Int? = null

    companion object {
        lateinit var notification: Uri
        lateinit var r: Ringtone
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onReceive(appContext: Context, intent: Intent) {
        val repo = Repositry.getInstance(
            WeatherClient.getInstance(appContext),
            LocalSourceImp.getInstance(appContext),
            PreferenceManager.getDefaultSharedPreferences(appContext)
        )
        val alertSettings = repo.getAlertSettings()
        val alertJson = intent.getStringExtra(Constants.Alert)
        val alert = Gson().fromJson(alertJson, AlertsData::class.java)
        val notificationHelper = NotificationHelper(appContext)
        notificationId = 1

        notificationManager = notificationHelper.alarmNotificationManager(appContext)

        Toast.makeText(appContext, "OnReceive alarm test", Toast.LENGTH_SHORT).show()
        CoroutineScope(Dispatchers.IO).launch {
            if (!Utils.isDaily(alert.startTime, alert.endTime)) {
                Utils.canelAlarm(appContext, alert.toString(), alert.startTime.toInt())
                repo.deleteAlert(alert)
                WorkManager.getInstance(appContext.applicationContext)
                    .cancelAllWorkByTag(alert.startTime.toString())
            }
            try {
                repo.allWeatherData(
                    lat = alert.lat,
                    lon = alert.lon,
                    "exclude",
                    "5e0f6f10e9b7bf48be1f3d781c3aa597"
                )
                    .collectLatest {
                        val bitmap = arrayOf<Bitmap?>(null)
                        Glide.with(appContext)
                            .asBitmap()
                            .load(
                                it.body()!!.current?.weather?.get(0)
                                    ?.let { it1 -> Utils.getIconUrl(it1.icon) })
                            .into(object : CustomTarget<Bitmap?>() {
                                @RequiresApi(Build.VERSION_CODES.S)
                                override fun onResourceReady(
                                    resource: Bitmap,
                                    @Nullable transition: Transition<in Bitmap?>?
                                ) {
                                    bitmap[0] = resource

                                    Log.e("onReceive", "onResourceReady: $resource")



                                    notification = RingtoneManager.getActualDefaultRingtoneUri(
                                        appContext.applicationContext,
                                        RingtoneManager.TYPE_ALARM
                                    )
                                    r = RingtoneManager.getRingtone(
                                        appContext.applicationContext,
                                        notification
                                    )
                                    if (alertSettings?.isALarm == true && !alertSettings.isNotification) {
                                        r.play()
                                        notificationManager.notify(notificationId!!,
                                            it.body()!!.current?.weather?.get(0)?.let { it1 ->
                                                notificationHelper.getNotification(
                                                    appContext,
                                                    notificationId!!,
                                                    Utils.getAddressEnglish(
                                                        appContext,
                                                        alert.lat,
                                                        alert.lon
                                                    ), it1.description, bitmap[0]!!
                                                )
                                            })


                                    }
                                    if (alertSettings?.isALarm == false && alertSettings.isNotification) {
                                        notificationManager.notify(
                                            notificationId!!,
                                            notificationHelper.getNotificationBuilder(
                                                Utils.getAddressEnglish(
                                                    appContext,
                                                    alert.lat,
                                                    alert.lon
                                                ),
                                                it.body()!!.current?.weather?.get(0)?.description,
                                                appContext,
                                                bitmap[0]!!

                                            ).build()
                                        )
                                    }

                                }

                                override fun onLoadCleared(@Nullable placeholder: Drawable?) {

                                }
                            })
                    }


            } finally {
                cancel()
            }
            repo.deleteAlert(alert)

        }


    }

}


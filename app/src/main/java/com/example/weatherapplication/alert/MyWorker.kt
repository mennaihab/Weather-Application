package com.example.weatherapplication.alert

import android.content.Context
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.weatherapplication.Constants
import com.example.weatherapplication.Utils
import com.example.weatherapplication.local.LocalSourceImp
import com.example.weatherapplication.models.AlertsData
import com.example.weatherapplication.remote.WeatherClient
import com.example.weatherapplication.repo.Repositry
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MyWorker(val appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val repository = Repositry.getInstance(
            WeatherClient.getInstance(appContext),
            LocalSourceImp.getInstance(appContext),
            PreferenceManager.getDefaultSharedPreferences(appContext)
        )
        val alertJson = inputData.getString(Constants.Alert)
        var alert = Gson().fromJson(alertJson, AlertsData::class.java)
        if (alert.endTime in alert.startTime..alert.endTime) {
            Log.i("date", alert.endTime.toString())
            setAlarm(alert.startTime, alertJson, alert.startTime.toInt())
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "daily worker", Toast.LENGTH_SHORT).show()
            }
        }

        if (alert.endTime < System.currentTimeMillis()) {
            WorkManager.getInstance(applicationContext)
                .cancelAllWorkByTag(alert.startTime.toString())
            repository.deleteAlert(alert)
            Utils.canelAlarm(applicationContext, alert.toString(), alert.startTime.toInt())
            withContext(Dispatchers.Main) {
                Toast.makeText(applicationContext, "your worker ended", Toast.LENGTH_SHORT).show()
            }
        }


        return Result.success()

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setAlarm(dateInMillis: Long, alert: String?, requestCode: Int) {
        var alarmMgr: AlarmManager? = null
        lateinit var alarmIntent: PendingIntent
        alarmMgr = applicationContext.getSystemService(ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(applicationContext, AlarmRecieverClass::class.java).putExtra(
            Constants.Alert,
            alert
        ).let { intent ->
            PendingIntent.getBroadcast(applicationContext, requestCode, intent, FLAG_IMMUTABLE)

        }
        alarmMgr?.setExact(
            AlarmManager.RTC_WAKEUP,
            dateInMillis,
            alarmIntent

        )


    }

}
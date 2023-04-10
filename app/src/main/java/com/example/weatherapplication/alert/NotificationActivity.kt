package com.example.weatherapplication.alert

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapplication.databinding.ActivityNotificationBinding


class NotificationActivity : AppCompatActivity() {
    var r= AlarmRecieverClass.r
    private var _binding: ActivityNotificationBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        }
        var notificationHelper=NotificationHelper(applicationContext)
        binding?.btnDismissFullScreenIntent?.setOnClickListener {
            r.stop()
            notificationHelper.alarmNotificationManager(applicationContext).cancel(1)
            finish()

        }

    }
}

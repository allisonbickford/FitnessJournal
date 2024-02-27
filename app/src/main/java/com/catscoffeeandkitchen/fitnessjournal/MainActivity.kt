package com.catscoffeeandkitchen.fitnessjournal

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.catscoffeeandkitchen.fitnessjournal.services.TimerService
import com.catscoffeeandkitchen.fitnessjournal.ui.navigation.Navigation
import com.catscoffeeandkitchen.ui.theme.LiftingLogTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var timerService by mutableStateOf(null as TimerService?)
    var isTimerServiceBound by mutableStateOf(false)

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // We've bound to TimerService, cast the IBinder and get LocalService instance.
            val binder = service as TimerService.TimerBinder
            timerService = binder.getService()
            isTimerServiceBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isTimerServiceBound = false
        }
    }

    private fun startTimerNotification(workoutId: Long, seconds: Long) {
        timerService?.let {
            timerService?.cancelTimer()
        }

        val intent = Intent(this, TimerService::class.java).apply {
            putExtra("seconds", seconds)
            putExtra("workoutId", workoutId)
        }

        this.startForegroundService(intent)
        isTimerServiceBound = this.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        if (isTimerServiceBound) {
            this.unbindService(connection)
        }
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        setContent {
            LiftingLogTheme {
                Navigation(
                    timerService = timerService,
                    onStartTimer = { workoutId, seconds ->
                        startTimerNotification(workoutId, seconds)
                    }
                )
            }
        }
    }
}

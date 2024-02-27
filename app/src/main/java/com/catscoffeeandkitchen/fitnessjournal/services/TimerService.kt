package com.catscoffeeandkitchen.fitnessjournal.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.catscoffeeandkitchen.common.ui.R
import com.catscoffeeandkitchen.fitnessjournal.services.TimerNotificationManager.Companion.CHANNEL_ID
import com.catscoffeeandkitchen.fitnessjournal.services.TimerNotificationManager.Companion.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerService: Service() {
    private val binder = TimerBinder()
    private var seconds = 0L
    private var startingSeconds = 0L

    private var timerJob: Job = Job()

    private var _secondsFlow: MutableSharedFlow<Long> = MutableSharedFlow()
    val secondsFlow: SharedFlow<Long> = _secondsFlow

    private var notificationManager: TimerNotificationManager? = null

    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)


    inner class TimerBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent): IBinder = binder

    @Suppress("MagicNumber")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val channel = NotificationChannel(CHANNEL_ID,
            getString(R.string.timer), NotificationManager.IMPORTANCE_NONE)
        channel.description = getString(R.string.timer_notification_channel_description)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                getString(R.string.timer),
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        val secondsInIntent = intent?.getLongExtra("seconds", 30L) ?: 30L
        val workoutId = intent?.getLongExtra("workoutId", 0L)
        seconds = secondsInIntent
        startingSeconds = secondsInIntent

        if (intent?.action == TimerNotificationManager.CANCEL_TIMER_ACTION) {
            cancelTimer()
        } else {
            val notificationManager = TimerNotificationManager()
            val notification = notificationManager.createNotification(this, workoutId, secondsInIntent, secondsInIntent)
            startForeground(SERVICE_NOTIFICATION_ID, notification)

            timerJob = coroutineScope.launch(Dispatchers.IO) {
                (0L..secondsInIntent)
                    .asFlow()
                    .onEach { delay(1_000) }
                    .onCompletion {
                        stopSelf()
                        stopForeground(STOP_FOREGROUND_REMOVE)
                    }
                    .collect { sec ->
                        val remaining = secondsInIntent - sec

                        _secondsFlow.emit(remaining)
                        seconds = remaining

//                        notificationManager.updateNotification(this@TimerService, workoutId, remaining, secondsInIntent)
                        if (remaining == 0L) {
                            val player = MediaPlayer.create(this@TimerService, R.raw.alert_chime)
                            player.start()
                            this@TimerService.stopSelf()
                            manager.cancel(NOTIFICATION_ID)
                        }
                    }
            }
        }

        return START_STICKY
    }

    fun setupNotificationManager(
        manager: TimerNotificationManager
    ) {
        notificationManager = manager
    }

    fun cancelTimer() {
        Timber.d("*** cancelling previous timer...")
        seconds = 0L
        timerJob.cancel()
    }

    companion object {
        const val SERVICE_NOTIFICATION_ID = 2
    }
}

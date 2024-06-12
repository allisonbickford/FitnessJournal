package com.catscoffeeandkitchen.fitnessjournal.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.catscoffeeandkitchen.fitnessjournal.MainActivity
import com.catscoffeeandkitchen.fitnessjournal.R
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen

class TimerNotificationManager {

    fun createNotification(context: Context, workoutId: Long?, seconds: Long, startingSeconds: Long): Notification {
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            data = if (workoutId != null) {
                Uri.parse("liftinglog://app/${LiftingLogScreen.WorkoutDetails.route}/$workoutId" )
            } else {
                Uri.parse("liftinglog://app/${LiftingLogScreen.WorkoutsScreen.route}" )
            }
        }
        .let { notificationIntent ->
            PendingIntent.getActivity(context, 0, notificationIntent,
                PendingIntent.FLAG_IMMUTABLE)
        }

        val cancelIntent = Intent(context, TimerService::class.java).apply {
            action = CANCEL_TIMER_ACTION
        }
        val cancelPendingIntent = PendingIntent.getForegroundService(
            context, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setUsesChronometer(true)
            .setChronometerCountDown(true)
            .setContentTitle(context.getString(R.string.x_seconds_on_timer, seconds.toString()))
            .setSmallIcon(R.drawable.fitness_center)
            .setContentIntent(mainIntent)
            .setSilent(seconds > 0)
            .setProgress(startingSeconds.toInt(), seconds.toInt(), false)
            .addAction(NotificationCompat.Action.Builder(
                R.drawable.remove, context.getString(R.string.cancel), cancelPendingIntent).build()
            )
            .setPriority(NotificationManager.IMPORTANCE_LOW)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setTimeoutAfter(3000)
            .build()
    }

    fun updateNotification(context: Context, workoutId: Long?, seconds: Long, startingSeconds: Long) {
        val notification = createNotification(context, workoutId, seconds, startingSeconds)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 1
        const val CHANNEL_ID = "TimerChannel"
        const val CANCEL_TIMER_ACTION = "CANCEL_TIMER"
    }
}

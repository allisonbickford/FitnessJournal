package com.catscoffeeandkitchen.ui.services

import android.app.Notification
import android.content.Context

interface TimerNotificationManager {
    fun createNotification(
        context: Context,
        workoutId: Long?,
        seconds: Long,
        startingSeconds: Long
    ): Notification

    fun updateNotification(context: Context, workoutId: Long?, seconds: Long, startingSeconds: Long)

    companion object {
        const val CANCEL_TIMER_ACTION = "com.catscoffeeandkitchen.fitnessjournal.CancelTimer"
    }
}

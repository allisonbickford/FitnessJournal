package com.catscoffeeandkitchen.fitnessjournal.services

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import com.catscoffeeandkitchen.fitnessjournal.MainActivity
import com.catscoffeeandkitchen.fitnessjournal.R
import com.catscoffeeandkitchen.ui.navigation.LiftingLogScreen

class WorkoutAppWidgetProvider: AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Perform this loop procedure for each widget that belongs to this
        // provider.
        appWidgetIds.forEach { appWidgetId ->
            // Create an Intent to launch ExampleActivity.
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                /* context = */ context,
                /* requestCode = */  0,
                /* intent = */ Intent(context, MainActivity::class.java).apply {
                    data = Uri.parse("liftinglog://app/${LiftingLogScreen.WorkoutsScreen.route}" )
                },
                /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Get the layout for the widget and attach an onClick listener to
            // the button.
            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.timer_appwidget
            ).apply {
                setOnClickPendingIntent(R.id.set_done_button, pendingIntent)
            }

            // Tell the AppWidgetManager to perform an update on the current widget.
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

}
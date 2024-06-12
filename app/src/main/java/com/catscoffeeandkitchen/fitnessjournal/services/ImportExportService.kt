package com.catscoffeeandkitchen.fitnessjournal.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class ImportExportService: Service() {
    private val binder = ImportExportBinder()

    inner class ImportExportBinder : Binder() {
        // Return this instance of LocalService so clients can call public methods
        fun getService(): ImportExportService = this@ImportExportService
    }

    override fun onBind(p0: Intent?): IBinder = binder
}
package com.example.android.dessertclicker

import android.app.Application
import timber.log.Timber
/*
* Base class that contains the global configuration for the whole application
*  make sure to initialize it in the Manifest*/
class ClickerApplication:Application() {
    override fun onCreate() {
        super.onCreate()

        // Initializing Timber to handler the log messages
        Timber.plant(Timber.DebugTree())
    }
}
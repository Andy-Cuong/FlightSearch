package com.example.flightsearch

import android.app.Application
import com.example.flightsearch.data.AppContainer
import com.example.flightsearch.data.OfflineAppContainer

/**
 * Include this custom application class in the Android manifest file
 */
class FlightSearchApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = OfflineAppContainer(context = this)
    }
}
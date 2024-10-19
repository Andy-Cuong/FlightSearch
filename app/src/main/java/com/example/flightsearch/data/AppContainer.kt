package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * Dependencies container for the app
 */
interface AppContainer {
    val airportRepository: AirportRepository
    val favFlightRepository: FavoriteFlightRepository
    val userInputPreferencesRepository: UserInputPreferencesRepository
}

/**
 * Implementation for offline status
 */
class OfflineAppContainer(
    context: Context
) : AppContainer {
    override val airportRepository by lazy {
        OfflineAirportRepository(FlightSearchDatabase.getDatabase(context = context).airportDao())
    }

    override val favFlightRepository by lazy {
        OfflineFavoriteFlightRepository(FlightSearchDatabase.getDatabase(context = context).favFlightDao())
    }

    private val LAST_USER_INPUT_NAME = "last_user_input"
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = LAST_USER_INPUT_NAME
    )

    override val userInputPreferencesRepository by lazy {
        UserInputPreferencesRepository(dataStore = context.dataStore)
    }
}
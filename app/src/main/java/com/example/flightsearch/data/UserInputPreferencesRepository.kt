package com.example.flightsearch.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserInputPreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val LAST_USER_INPUT = stringPreferencesKey("last_user_input")
        const val TAG = "UserInputRepo" // Log tag
    }

    val lastUserInput: Flow<String> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e(TAG, "Error reading last user input")
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences -> preferences[LAST_USER_INPUT] ?: "" }

    suspend fun saveLastUserInput(input: String) {
        dataStore.edit { preferences ->
            preferences[LAST_USER_INPUT] = input
        }
    }
}
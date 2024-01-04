package com.example.greenie

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PreferencesDataStore(
    var context: Context
) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

        private val DARK_MODE = booleanPreferencesKey("darkMode")
        private val DARK_MODE_SYSTEM = booleanPreferencesKey("darkModeSystem")
        private val FIRST_LAUNCH = booleanPreferencesKey("firstLaunch")
    }

    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: true
        }

    suspend fun setDarkMode(boolean: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = boolean
        }
    }


    val darkModeSystem: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE_SYSTEM] ?: true
        }

    suspend fun setDarkModeSystem(boolean: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_SYSTEM] = boolean
        }
    }


    val firstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[FIRST_LAUNCH] ?: true
        }

    suspend fun setFirstLaunch(boolean: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = boolean
        }
    }
}
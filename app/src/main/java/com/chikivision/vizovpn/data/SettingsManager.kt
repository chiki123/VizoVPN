package com.chikivision.vizovpn.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Create the DataStore instance
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsManager @Inject constructor(@ApplicationContext private val context: Context) {

    // Define keys for our settings
    companion object {
        val KILL_SWITCH_ENABLED = booleanPreferencesKey("kill_switch_enabled")
    }

    // Read the Kill Switch setting as a Flow
    val isKillSwitchEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[KILL_SWITCH_ENABLED] ?: false
        }

    // Write a new value for the Kill Switch setting
    suspend fun setKillSwitchEnabled(isEnabled: Boolean) {
        context.dataStore.edit { settings ->
            settings[KILL_SWITCH_ENABLED] = isEnabled
        }
    }
}
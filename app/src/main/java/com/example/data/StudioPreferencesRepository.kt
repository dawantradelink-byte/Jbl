package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.MotionPreference
import com.example.ui.theme.StudioTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "studio_settings")

class StudioPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_ID = stringPreferencesKey("theme_id")
        val MOTION_PREFERENCE = stringPreferencesKey("motion_preference")
        val VOLUME_LIMITER = booleanPreferencesKey("volume_limiter")
        val HAPTICS_ENABLED = booleanPreferencesKey("haptics_enabled")
    }

    val activeThemeFlow: Flow<StudioTheme> = context.dataStore.data.map { preferences ->
        val id = preferences[PreferencesKeys.THEME_ID] ?: StudioTheme.SUNSET_GLOW.id
        StudioTheme.fromId(id)
    }

    val motionPreferenceFlow: Flow<MotionPreference> = context.dataStore.data.map { preferences ->
        val mode = preferences[PreferencesKeys.MOTION_PREFERENCE] ?: MotionPreference.FULL.name
        try {
            MotionPreference.valueOf(mode)
        } catch (_: Exception) {
            MotionPreference.FULL
        }
    }

    val volumeLimiterFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.VOLUME_LIMITER] ?: true
    }

    val hapticsEnabledFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAPTICS_ENABLED] ?: true
    }

    suspend fun setTheme(theme: StudioTheme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_ID] = theme.id
        }
    }

    suspend fun setMotionPreference(preference: MotionPreference) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.MOTION_PREFERENCE] = preference.name
        }
    }

    suspend fun setVolumeLimiter(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.VOLUME_LIMITER] = enabled
        }
    }

    suspend fun setHapticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAPTICS_ENABLED] = enabled
        }
    }
}

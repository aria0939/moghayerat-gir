package com.damdarangha.moghayerat.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "settings")

/**
 * ذخیره تنظیمات برنامه با DataStore Preferences.
 *
 * تنظیمات شامل:
 * - تم روشن/تاریک
 * - تاریخ آخرین آمار ثبت‌شده
 * - وضعیت آمارگیری روز جاری
 */
class SettingsDataStore(private val context: Context) {

    private val keyTheme = stringPreferencesKey("theme_mode") // LIGHT, DARK, SYSTEM
    private val keyLastStatDate = stringPreferencesKey("last_stat_date")
    private val keyAutoSave = booleanPreferencesKey("auto_save")
    private val keyCarryOverConfirmed = booleanPreferencesKey("carry_over_confirmed")

    val themeMode: Flow<ThemeMode> = context.settingsDataStore.data.map { prefs ->
        when (prefs[keyTheme] ?: "SYSTEM") {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val lastStatDate: Flow<String?> = context.settingsDataStore.data.map { prefs ->
        prefs[keyLastStatDate]
    }

    val autoSaveEnabled: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[keyAutoSave] ?: true
    }

    val carryOverConfirmed: Flow<Boolean> = context.settingsDataStore.data.map { prefs ->
        prefs[keyCarryOverConfirmed] ?: false
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.settingsDataStore.edit { prefs ->
            prefs[keyTheme] = mode.name
        }
    }

    suspend fun setLastStatDate(date: String) {
        context.settingsDataStore.edit { prefs ->
            prefs[keyLastStatDate] = date
        }
    }

    suspend fun setAutoSave(enabled: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[keyAutoSave] = enabled
        }
    }

    suspend fun setCarryOverConfirmed(confirmed: Boolean) {
        context.settingsDataStore.edit { prefs ->
            prefs[keyCarryOverConfirmed] = confirmed
        }
    }
}

enum class ThemeMode { LIGHT, DARK, SYSTEM }

package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.model.ThemeSettings

const val DARK_THEME_KEY = "key_for_dark_theme"
const val DARK_THEME_PREFERENCES = "dark_theme_preferences"
class SettingsRepositoryImpl(private val sharedPrefs: SharedPreferences) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(isDarkThemeEnabled = sharedPrefs.getBoolean(DARK_THEME_KEY, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, settings.isDarkThemeEnabled).apply()
    }
}
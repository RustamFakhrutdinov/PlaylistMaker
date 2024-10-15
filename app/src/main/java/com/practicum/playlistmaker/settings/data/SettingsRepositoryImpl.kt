package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import com.practicum.playlistmaker.settings.model.ThemeSettings

const val DARK_THEME_KEY = "key_for_dark_theme"
const val DARK_THEME_PREFERENCES = "dark_theme_preferences"
class SettingsRepositoryImpl(context: Context) : SettingsRepository {

    private val sharedPrefs = context.getSharedPreferences(DARK_THEME_PREFERENCES, Application.MODE_PRIVATE)
//    override fun saveDarkThemeState(state: Boolean) {
//        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, state).apply()
//    }
//
//    override fun getDarkThemeState(): Boolean {
//       return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
//    }

    override fun getThemeSettings(): ThemeSettings {
        return ThemeSettings(isDarkThemeEnabled = sharedPrefs.getBoolean(DARK_THEME_KEY, false))
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, settings.isDarkThemeEnabled).apply()
    }
}
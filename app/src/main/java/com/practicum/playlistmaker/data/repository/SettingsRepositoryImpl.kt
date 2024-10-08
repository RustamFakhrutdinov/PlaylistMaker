package com.practicum.playlistmaker.data.repository

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.domain.settings.SettingsRepository

const val DARK_THEME_KEY = "key_for_dark_theme"
const val DARK_THEME_PREFERENCES = "dark_theme_preferences"
class SettingsRepositoryImpl(context: Context) : SettingsRepository{

    private val sharedPrefs = context.getSharedPreferences(DARK_THEME_PREFERENCES, Application.MODE_PRIVATE)
    override fun saveDarkThemeState(state: Boolean) {
        sharedPrefs.edit().putBoolean(DARK_THEME_KEY, state).apply()
    }

    override fun getDarkThemeState(): Boolean {
       return sharedPrefs.getBoolean(DARK_THEME_KEY, false)
    }
}
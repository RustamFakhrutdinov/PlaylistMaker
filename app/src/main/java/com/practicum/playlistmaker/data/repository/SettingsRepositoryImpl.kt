package com.practicum.playlistmaker.data.repository

import android.content.Context
import com.practicum.playlistmaker.domain.settings.SettingsRepository

const val DARK_THEME_PREFERENCES = "dark_theme_preferences"
const val DARK_THEME_KEY = "key_for_dark_theme"

class SettingsRepositoryImpl(context: Context) : SettingsRepository{
    private val sharedPreferences = context.getSharedPreferences(DARK_THEME_PREFERENCES, Context.MODE_PRIVATE)
    override fun saveDarkThemeState(state: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME_KEY, state).apply()
    }

    override fun getDarkThemeState(): Boolean {
       return sharedPreferences.getBoolean(DARK_THEME_KEY, false)
    }
}
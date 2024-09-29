package com.practicum.playlistmaker.domain.settings

import android.content.SharedPreferences

interface SettingsRepository {
    fun saveDarkThemeState(state:Boolean)

    fun getDarkThemeState(): Boolean
}
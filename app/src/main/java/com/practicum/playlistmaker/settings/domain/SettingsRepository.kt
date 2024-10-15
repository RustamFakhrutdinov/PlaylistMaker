package com.practicum.playlistmaker.settings.domain

import com.practicum.playlistmaker.settings.model.ThemeSettings

interface SettingsRepository {
    fun getThemeSettings(): ThemeSettings
    fun updateThemeSetting(settings: ThemeSettings)
}
package com.practicum.playlistmaker.domain.settings

interface SettingsRepository {
    fun saveDarkThemeState(state:Boolean)

    fun getDarkThemeState(): Boolean
}
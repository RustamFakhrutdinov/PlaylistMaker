package com.practicum.playlistmaker.domain.settings

interface SettingsInteractor {
    fun saveDarkThemeState(state:Boolean)

    fun getDarkThemeState(): Boolean

}
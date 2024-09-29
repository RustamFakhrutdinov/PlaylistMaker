package com.practicum.playlistmaker.domain.settings

import android.content.SharedPreferences

interface SettingsInteractor {
    fun saveDarkThemeState(state:Boolean)

    fun getDarkThemeState(): Boolean

}
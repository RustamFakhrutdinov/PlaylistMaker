package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun saveDarkThemeState(state: Boolean) {
        repository.saveDarkThemeState(state)
    }

    override fun getDarkThemeState(): Boolean {
       return repository.getDarkThemeState()
    }

}
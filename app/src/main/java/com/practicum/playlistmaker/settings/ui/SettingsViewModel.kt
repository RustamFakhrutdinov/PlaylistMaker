package com.practicum.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.util.App


class SettingsViewModel(private val settingsInteractor: SettingsInteractor,
                        private val sharingInteractor: SharingInteractor
): ViewModel() {


    private val settingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeState(): LiveData<ThemeSettings> = settingsLiveData
    init {
        switchTheme(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(settings: ThemeSettings) {
        settingsInteractor.updateThemeSetting(settings)
        (App.getAppContext() as App).switchTheme(settings.isDarkThemeEnabled)
        settingsLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }
    fun openSupport() {
        sharingInteractor.openSupport()
    }

    companion object {
    }



}
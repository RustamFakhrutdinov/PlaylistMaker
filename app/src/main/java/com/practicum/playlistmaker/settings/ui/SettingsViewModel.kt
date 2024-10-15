package com.practicum.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.model.ThemeSettings
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.util.Creator

class SettingsViewModel(context: Context): ViewModel() {


    private val settingsLiveData = MutableLiveData<ThemeSettings>()
    fun observeState(): LiveData<ThemeSettings> = settingsLiveData
    private val settingsInteractor = Creator.provideSettingsInteractor()
    private val searchInteractor = Creator.provideSharingInteractor(context)

    init {
        switchTheme(settingsInteractor.getThemeSettings())
    }

    fun switchTheme(settings: ThemeSettings) {
        settingsInteractor.updateThemeSetting(settings)
        (App.getAppContext() as App).switchTheme(settings.isDarkThemeEnabled)
        settingsLiveData.postValue(settingsInteractor.getThemeSettings())
    }

    fun shareApp() {
        searchInteractor.shareApp()
    }

    fun openTerms() {
        searchInteractor.openTerms()
    }
    fun openSupport() {
        searchInteractor.openSupport()
    }

    companion object {
        fun getViewModelFactory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(context)
            }
        }
    }



}
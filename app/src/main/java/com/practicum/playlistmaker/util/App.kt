package com.practicum.playlistmaker.util

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate


class App: Application() {
    companion object {
        private lateinit var instance: App

        fun getAppContext(): Context {
            return instance.applicationContext
        }
    }

   private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        instance = this
        darkTheme = Creator.provideSettingsInteractor().getThemeSettings().isDarkThemeEnabled

        if(darkTheme) {
            switchTheme(true)
        }




    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
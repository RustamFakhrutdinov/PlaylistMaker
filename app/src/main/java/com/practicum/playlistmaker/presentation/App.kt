package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.ui.settings.DARK_THEME_KEY
import com.practicum.playlistmaker.ui.settings.DARK_THEME_PREFERENCES


class App: Application() {

   private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        val sharedPrefs = getSharedPreferences(DARK_THEME_PREFERENCES, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(DARK_THEME_KEY, false)

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
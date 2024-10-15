package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.util.App
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.model.ThemeSettings


class SettingsActivity : AppCompatActivity() {
    private val settingsInteractor = Creator.provideSettingsInteractor()

    private lateinit var viewModel: SettingsViewModel

    private lateinit var binding: ActivitySettingsBinding

    private lateinit var shareButton: Button
    private lateinit var supportButton: Button
    private lateinit var userAgreementButton: Button
    private lateinit var backButton: Button
    private lateinit var themeSwitcher: SwitchMaterial



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shareButton = binding.share
        supportButton = binding.support
        userAgreementButton = binding.userAgreement
        backButton = binding.arrowBack
        themeSwitcher = binding.themeSwitcher

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory(this))[SettingsViewModel::class.java]

        backButton.setOnClickListener {
            finish()
        }

        viewModel.observeState().observe(this) {
            themeSwitcher.isChecked = it.isDarkThemeEnabled
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            val themeSettings = ThemeSettings(checked)
            viewModel.switchTheme(themeSettings)
        }

        shareButton.setOnClickListener {
            viewModel.shareApp()
        }

        supportButton.setOnClickListener {
            viewModel.openSupport()
        }

        userAgreementButton.setOnClickListener {
            viewModel.openTerms()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        themeSwitcher.setOnCheckedChangeListener(null)
    }


}
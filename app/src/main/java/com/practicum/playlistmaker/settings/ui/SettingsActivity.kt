package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.model.ThemeSettings
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    private lateinit var shareButton: Button
    private lateinit var supportButton: Button
    private lateinit var userAgreementButton: Button
    private lateinit var backButton: Button
    private lateinit var themeSwitcher: SwitchMaterial


    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shareButton = binding.share
        supportButton = binding.support
        userAgreementButton = binding.userAgreement
        backButton = binding.arrowBack
        themeSwitcher = binding.themeSwitcher

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
package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.mediateka.ui.MediatekaFragment
import com.practicum.playlistmaker.mediateka.ui.MediatekaViewPagerAdapter
import com.practicum.playlistmaker.settings.model.ThemeSettings
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private lateinit var binding: FragmentSettingsBinding

    private lateinit var shareButton: Button
    private lateinit var supportButton: Button
    private lateinit var userAgreementButton: Button
    private lateinit var backButton: Button
    private lateinit var themeSwitcher: SwitchMaterial


    private val viewModel: SettingsViewModel by viewModel()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        shareButton = binding.share
        supportButton = binding.support
        userAgreementButton = binding.userAgreement
        backButton = binding.arrowBack
        themeSwitcher = binding.themeSwitcher

//        backButton.setOnClickListener {
//            finish()
//        }

        viewModel.observeState().observe(viewLifecycleOwner) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        themeSwitcher.setOnCheckedChangeListener(null)
    }

    companion object {


        // Тег для использования во FragmentManager
        const val TAG = "SettingsFragment"

        fun createArgs(): Bundle =
            bundleOf()

    }
}
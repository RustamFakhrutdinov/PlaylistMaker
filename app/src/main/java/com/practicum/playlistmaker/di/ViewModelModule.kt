package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediateka.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get(), get(),androidContext())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel(get(),androidContext())
    }

    viewModel {
        PlaylistViewModel(get(),androidContext(),get())
    }

}
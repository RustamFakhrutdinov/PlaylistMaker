package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediateka.data.reposirtory.FavouriteRepositoryImpl
import com.practicum.playlistmaker.mediateka.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.data.converters.TrackFavouriteDbConverter
import com.practicum.playlistmaker.mediateka.data.reposirtory.PlaylistRepositoryImpl
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteRepository
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.player.data.PlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.data.history.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(sharedPrefs = get(named("historyPrefs")), get(),get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get(),get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPrefs = get(named("themePrefs")))
    }

    factory { TrackFavouriteDbConverter() }

    factory { PlaylistDbConverter() }

    single<FavouriteRepository> {
        FavouriteRepositoryImpl(get(),get())
    }

    single<PlaylistRepository> {
        PlaylistRepositoryImpl(get(),get())
    }


}
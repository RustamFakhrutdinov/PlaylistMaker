package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.network.TracksRepositoryImpl
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.SettingsRepositoryImpl
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.settings.SettingsInteractor
import com.practicum.playlistmaker.domain.settings.SettingsRepository

object Creator {
    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getSearchHistoryRepository(context: Context) : SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

     fun provideSearchHistoryInteractor(context: Context) : SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository(context))
    }

    private fun getSettingsRepository(context: Context) : SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    fun provideSettingsInteractor(context: Context) : SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository(context))
    }
}
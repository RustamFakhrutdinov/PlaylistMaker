package com.practicum.playlistmaker.domain.impl

import android.content.SharedPreferences
import com.practicum.playlistmaker.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository): SearchHistoryInteractor {
    override fun saveToHistory(tracks: ArrayList<Track>) {
        repository.saveToHistory(tracks)
    }

    override fun readFromHistory(): ArrayList<Track> {
        return repository.readFromHistory()
    }

}
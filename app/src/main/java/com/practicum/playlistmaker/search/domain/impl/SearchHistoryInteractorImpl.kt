package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):
    SearchHistoryInteractor {
    override fun saveToHistory(track: Track) {
        repository.saveToHistory(track)
    }

    override fun readFromHistory(): ArrayList<Track> {
        return repository.readFromHistory()
    }

    override fun clearSearchHistory() {
        return repository.clearSearchHistory()
    }


}
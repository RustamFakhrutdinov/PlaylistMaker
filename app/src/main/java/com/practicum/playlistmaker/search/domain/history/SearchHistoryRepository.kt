package com.practicum.playlistmaker.search.domain.history

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
       suspend fun saveToHistory(track: Track)
        fun readFromHistory(): ArrayList<Track>

        fun clearSearchHistory()
}
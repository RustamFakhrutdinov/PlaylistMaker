package com.practicum.playlistmaker.domain.history

import com.practicum.playlistmaker.domain.models.Track

interface SearchHistoryInteractor {
    fun saveToHistory(tracks: ArrayList<Track>)

    fun readFromHistory(): ArrayList<Track>
}
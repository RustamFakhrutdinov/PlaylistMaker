package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface TracksRepository {
    fun search(expression: String): Resource<List<Track>>
}
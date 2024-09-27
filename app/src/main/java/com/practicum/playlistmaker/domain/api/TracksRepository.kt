package com.practicum.playlistmaker.domain.api

import android.content.Context
import com.practicum.playlistmaker.domain.models.Track

interface TracksRepository {
    fun search(expression: String): List<Track>

}
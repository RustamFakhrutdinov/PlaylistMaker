package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerRepository {
    fun isPlaying(): Boolean
    fun preparePlayer(url: String)

    fun getTime(): String

    fun release()

    fun loadTrackData(): Track

    fun play()

    fun pause()
}
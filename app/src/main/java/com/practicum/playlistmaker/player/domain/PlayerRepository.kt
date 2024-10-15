package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerRepository {
    fun preparePlayer()

    fun getTime(): String

    fun release()

    fun loadTrackData(): Track

    fun play(status: PlayerInteractor.StatusObserver)

    fun pause()
}
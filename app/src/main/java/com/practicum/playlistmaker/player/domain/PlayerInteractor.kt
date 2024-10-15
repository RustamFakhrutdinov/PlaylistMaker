package com.practicum.playlistmaker.player.domain

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerInteractor {
    fun preparePlayer()

    fun getTime(): String
    fun loadTrackData(): Track

    fun play(status: StatusObserver)
    fun pause()

    fun release()


    interface StatusObserver {
        fun onProgress(progress: String)
        fun onStop()
        fun onPlay()
    }
}
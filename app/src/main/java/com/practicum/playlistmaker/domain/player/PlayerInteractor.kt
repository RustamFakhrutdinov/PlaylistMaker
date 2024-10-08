package com.practicum.playlistmaker.domain.player

interface PlayerInteractor {
    fun startPlayer(listener: MediaPlayerListener)

    fun pausePlayer(listener: MediaPlayerListener)

    fun preparePlayer(previewUrl: String, listener: MediaPlayerListener)

    fun getTime(): String

    fun playbackControl(listener: MediaPlayerListener)

    fun releaseMediaPlayer()
}
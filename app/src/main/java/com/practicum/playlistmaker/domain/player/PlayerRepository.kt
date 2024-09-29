package com.practicum.playlistmaker.domain.player

import android.media.MediaPlayer

interface PlayerRepository {
    fun startPlayer(listener: MediaPlayerListener)

    fun pausePlayer(listener: MediaPlayerListener)

    fun preparePlayer(previewUrl: String, listener: MediaPlayerListener)

    fun getTime(): String

    fun playbackControl(listener: MediaPlayerListener)

    fun getMediaPlayer(): MediaPlayer

}
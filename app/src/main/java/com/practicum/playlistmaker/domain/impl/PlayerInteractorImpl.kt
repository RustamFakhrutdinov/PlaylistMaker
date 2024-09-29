package com.practicum.playlistmaker.domain.impl

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.player.MediaPlayerListener
import com.practicum.playlistmaker.domain.player.PlayerInteractor
import com.practicum.playlistmaker.domain.player.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository): PlayerInteractor {
    override fun startPlayer(listener: MediaPlayerListener) {
        repository.startPlayer(listener)
    }

    override fun pausePlayer(listener: MediaPlayerListener) {
        repository.pausePlayer(listener)
    }

    override fun preparePlayer(previewUrl: String, listener: MediaPlayerListener) {
        repository.preparePlayer(previewUrl, listener)
    }

    override fun getTime(): String {
       return repository.getTime()
    }

    override fun playbackControl(listener: MediaPlayerListener) {
       repository.playbackControl(listener)
    }

    override fun getMediaPlayer(): MediaPlayer {
        return repository.getMediaPlayer()
    }
}
package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.player.MediaPlayerListener
import com.practicum.playlistmaker.domain.player.PlayerRepository
import com.practicum.playlistmaker.presentation.App
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl: PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    override fun startPlayer(listener: MediaPlayerListener) {
        mediaPlayer.start()
        listener.onPlayerStart()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer(listener: MediaPlayerListener) {
        listener.onPlayerPause()
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun preparePlayer(previewUrl: String, listener: MediaPlayerListener) {
        if (previewUrl != "No previewUrl") {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                prepareAsync()
                setOnPreparedListener {
                    playerState = STATE_PREPARED
                }
                setOnCompletionListener {
                    listener.onPlayerCompletion()
                    playerState = STATE_PREPARED
                }
            }
        }

    }

   override fun getTime(): String {
       return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun playbackControl(listener: MediaPlayerListener) {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer(listener)
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(listener)
            }
        }
    }

    override fun getMediaPlayer(): MediaPlayer {
        return mediaPlayer
    }


    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }
}
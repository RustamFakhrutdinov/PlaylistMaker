package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer,
                           private val searchHistoryInteractor: SearchHistoryInteractor): PlayerRepository {
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun preparePlayer() {
        val previewUrl = loadTrackData().previewUrl
        if (previewUrl != "No previewUrl") {
            try {
                mediaPlayer.setDataSource(previewUrl)
                mediaPlayer.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
   override fun getTime(): String {
       return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun release() {
        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
    }

    override fun loadTrackData(): Track {
        return searchHistoryInteractor.readFromHistory()[0]
    }

override fun play() {
    mediaPlayer.apply {
        start()
    }
}
    override fun pause() {
        mediaPlayer.pause()
    }
}

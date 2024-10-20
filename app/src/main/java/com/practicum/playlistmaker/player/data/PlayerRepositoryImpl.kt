package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer,
                           private val searchHistoryInteractor: SearchHistoryInteractor): PlayerRepository {
    private val handler = Handler(Looper.getMainLooper())
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
        handler?.removeCallbacksAndMessages(null)
    }

    override fun loadTrackData(): Track {
        return searchHistoryInteractor.readFromHistory()[0]
    }

    override fun play(status: PlayerInteractor.StatusObserver) {
        mediaPlayer.apply {
            start()
            status.onPlay()
            handler?.removeCallbacksAndMessages(null)
            val updateProgressTask = object : Runnable {
                override fun run() {
                    if (mediaPlayer != null) {
                        try {
                            if (mediaPlayer.isPlaying) {
                                val progress = getTime()
                                status.onProgress(progress)
                                handler.postDelayed(this, 500)
                            }
                        } catch (e: IllegalStateException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            handler.post(updateProgressTask)

            setOnCompletionListener {
                handler?.removeCallbacksAndMessages(null)
                status.onStop()
                status.onProgress("00:00")
            }
        }
    }
    override fun pause() {
        mediaPlayer.pause()
    }
}

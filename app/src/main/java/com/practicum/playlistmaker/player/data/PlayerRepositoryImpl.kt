package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Creator
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl: PlayerRepository {

    private var mediaPlayer = MediaPlayer()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()
    private val handler = Handler(Looper.getMainLooper())

    override fun preparePlayer() {
        val previewUrl = loadTrackData().previewUrl
        if (previewUrl != "No previewUrl") {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(previewUrl)
                prepare()
            }
        }
    }

   override fun getTime(): String {
       return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun release() {
        mediaPlayer.release()
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
                    if (mediaPlayer?.isPlaying == true) {
                        val progress = getTime()
                        status.onProgress(progress)
                        handler.postDelayed(this, 500)
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

package com.practicum.playlistmaker.player.data

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer,
                           private val searchHistoryInteractor: SearchHistoryInteractor
): PlayerRepository {
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun preparePlayer(url: String) {
        if (url != "No previewUrl") {
            try {
                mediaPlayer.setDataSource(url)
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

package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerViewModel(private val playerInteractor: PlayerInteractor
) : ViewModel() {
//    private val playerInteractor = Creator.providePlayerInteractor()
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData


    init {
        preparePlayer()
    }

    fun loadTrackData(): Track {
        return playerInteractor.loadTrackData()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun play() {

        playerInteractor.play(
            status = object : PlayerInteractor.StatusObserver {
                override fun onProgress(progress: String) {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(progress = progress)
                }

                override fun onStop() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = false)
                }

                override fun onPlay() {
                    playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
                }

            }
        )
    }

    fun pause() {
        playerInteractor.pause()
        playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false)
    }


    fun release() {
        playerInteractor.release()
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer()
    }

}
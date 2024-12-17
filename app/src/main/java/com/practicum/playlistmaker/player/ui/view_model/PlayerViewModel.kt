package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor
) : ViewModel() {
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData


    init {
        preparePlayer()
    }
    private var timerJob: Job? = null
    fun loadTrackData(): Track {
        return playerInteractor.loadTrackData()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun play() {
        playerInteractor.play()
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
        startTimer()
    }
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(300L)
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(progress = playerInteractor.getTime()))
            }
            playStatusLiveData.postValue(getCurrentPlayStatus().copy(progress = "00:00", isPlaying = false))
        }
    }


    fun pause() {
        playerInteractor.pause()
        timerJob?.cancel()
        playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false)
    }

    private fun release() {
        playerInteractor.release()
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer()
    }

}
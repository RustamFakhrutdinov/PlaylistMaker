package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor,
    private val favouriteInteractor: FavouriteInteractor
) : ViewModel() {
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData


    init {
        preparePlayer()
    }
    private var timerJob: Job? = null
    fun isFavourite(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            if(favouriteInteractor.isFavourite(track.trackId!!)) {
                track.isFavorite = true
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isFavourite = true))
            } else {
                track.isFavorite = false
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isFavourite = false))
            }
        }

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
        return playStatusLiveData.value ?: PlayStatus(progress = "00:00", isPlaying = false, isFavourite = false)
    }

    private fun release() {
        playerInteractor.release()
    }

    private fun preparePlayer() {
        playerInteractor.preparePlayer()
    }

    fun onFavoriteClicked(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            if(!track.isFavorite) {
                track.isFavorite = true
                favouriteInteractor.addTrackToFavourite(track)
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isFavourite = true))
            } else {
                track.isFavorite = false//
                favouriteInteractor.deleteTrackFromFavourite(track)
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(isFavourite = false))
            }
        }


    }

}
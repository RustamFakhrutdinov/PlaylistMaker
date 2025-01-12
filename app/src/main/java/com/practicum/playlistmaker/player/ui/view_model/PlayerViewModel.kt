package com.practicum.playlistmaker.player.ui.view_model

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.state.PlaylistState
import com.practicum.playlistmaker.mediateka.ui.state.TrackInPlaylistState
import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragmentDirections
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val playerInteractor: PlayerInteractor,
    private val favouriteInteractor: FavouriteInteractor,
    private val playlistInteractor: PlaylistInteractor,
                      private val context: Context
) : ViewModel() {
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    private val playlistLiveData = MutableLiveData<PlaylistState>()
    fun getPlaylistLiveData(): LiveData<PlaylistState> = playlistLiveData

    private val trackInPlaylistLiveData = MutableLiveData<TrackInPlaylistState>()
    fun getTrackInPlaylistLiveData(): LiveData<TrackInPlaylistState> = trackInPlaylistLiveData


    init {
        preparePlayer()
    }
    private var timerJob: Job? = null

    private lateinit var clickDebounce: (Track) -> Unit
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

    fun fillPlaylistData() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect{ playlists ->
                    processResult(playlists)
                }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Empty(context.getString(R.string.no_playlists)))
        } else {
            renderState(PlaylistState.Content(playlists))
        }
    }
    private fun renderState(state: PlaylistState) {
        playlistLiveData.postValue(state)
    }

    private fun readTrackListFromBD(trackList: String): ArrayList<Int> {

        val json: String? = trackList
        val itemType = object : TypeToken<List<Int>>() {}.type

        return Gson().fromJson(json, itemType)
    }

    fun onPlaylistClicked(track: Track, playlist: Playlist) {

        if(playlist.trackIdList != null) {
            val trackList = readTrackListFromBD(playlist.trackIdList!!)
            if (trackList.contains(track.trackId)) {
                trackInPlaylistLiveData.postValue(TrackInPlaylistState.NotAdded(playlist.name))
            } else {
                viewModelScope.launch(Dispatchers.IO) {
                    playlistInteractor.addTrackIdToPlaylist(track, playlist.playlistId)
                    trackInPlaylistLiveData.postValue(TrackInPlaylistState.Added(playlist.name))
                }

            }
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                playlistInteractor.addTrackIdToPlaylist(track, playlist.playlistId)
                trackInPlaylistLiveData.postValue(TrackInPlaylistState.Added(playlist.name))
            }

        }

    }



}
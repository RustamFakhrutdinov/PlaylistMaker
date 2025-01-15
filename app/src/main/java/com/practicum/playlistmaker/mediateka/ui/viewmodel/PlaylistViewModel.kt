package com.practicum.playlistmaker.mediateka.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.state.FavouriteTracksState
import com.practicum.playlistmaker.mediateka.ui.state.PlaylistState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val context: Context
) : ViewModel() {
    private val playlistLiveData = MutableLiveData<PlaylistState>()
    fun getPlaylistLiveData(): LiveData<PlaylistState> = playlistLiveData

    private var tracksLiveData = MutableLiveData<FavouriteTracksState>()
    fun getTracksLiveData(): LiveData<FavouriteTracksState> = tracksLiveData

    fun loadTrackList(playlistId:Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getTrackListFromPlaylist(playlistId)
                .collect{tracks ->
                    processResultForTrackList(tracks)
                }
        }
    }
    private fun processResultForTrackList(tracks: List<Track>) {
        if (tracks.isNullOrEmpty()) {
            renderStateForTracks(FavouriteTracksState.Empty(context.getString(R.string.no_tracks_in_playlist)))
        } else {
            renderStateForTracks(FavouriteTracksState.Content(tracks))
        }
    }
    private fun renderStateForTracks(state: FavouriteTracksState) {
        tracksLiveData.postValue(state)
    }
    fun fillData() {
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

    fun addPlaylist(name: String, description: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.addPlaylist(Playlist(0,name,description,null,null,0),uri)
        }
    }
}
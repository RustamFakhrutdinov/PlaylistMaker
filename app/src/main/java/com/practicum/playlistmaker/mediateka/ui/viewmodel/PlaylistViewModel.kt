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
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val context: Context,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val playlistLiveData = MutableLiveData<PlaylistState>()
    fun getPlaylistLiveData(): LiveData<PlaylistState> = playlistLiveData

    private val onePlaylistLiveData = MutableLiveData<Playlist>()
    fun getOnePlaylistLiveData(): LiveData<Playlist> = onePlaylistLiveData

    private var tracksLiveData = MutableLiveData<FavouriteTracksState>()
    fun getTracksLiveData(): LiveData<FavouriteTracksState> = tracksLiveData

    private var newPlaylistLiveData = MutableLiveData<Playlist>()
    fun getNewPlaylistLiveData(): LiveData<Playlist> = newPlaylistLiveData

    //обработка треков в плейлисте
    fun loadTrackList(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getTrackListFromPlaylist(playlistId)
                .collect { tracks ->
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
    //обработка треков в плейлисте

    //обработка плейлистов на экране PlaylistFragment
    fun fillPlaylistsData() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylists()
                .collect { playlists ->
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
    //обработка плейлистов на экране PlaylistFragment


    //обработка выбранного плейлиста на экране ChosenPlaylistFragment
    fun fillOnePlaylistData(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor
                .getPlaylistById(playlistId)
                ?.collect { playlist ->
                    renderPlaylist(playlist)
                }
        }
    }

    private fun renderPlaylist(playlist: Playlist?) {
        if (playlist != null) {
            onePlaylistLiveData.postValue(playlist!!)
        }

    }
    //обработка выбранного плейлиста на экране ChosenPlaylistFragment


    fun addPlaylist(name: String, description: String, uri: Uri?) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.addPlaylist(Playlist(0, name, description, null, null, 0), uri)
        }
    }

    fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deleteTrackFromPlaylist(track, playlistId)
            loadTrackList(playlistId)
        }

    }

    fun sharePlaylist(playlist: Playlist, tracks: List<Track>) {
        sharingInteractor.sharePlaylist(playlist, tracks)
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.deletePlaylist(playlistId)
        }
    }

    fun editPlaylist(
        playlistId: Int,
        newName: String,
        newDescription: String?,
        newPath: Uri?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistInteractor.editPlaylist(playlistId, newName, newDescription, newPath)
        }
    }
}
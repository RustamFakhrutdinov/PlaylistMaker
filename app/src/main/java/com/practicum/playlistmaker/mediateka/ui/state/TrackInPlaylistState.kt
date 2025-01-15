package com.practicum.playlistmaker.mediateka.ui.state

import com.practicum.playlistmaker.mediateka.domain.models.Playlist

sealed interface TrackInPlaylistState {

    data class Added(
        val playlistName: String
    ) : TrackInPlaylistState

    data class NotAdded(
        val playlistName: String
    ) : TrackInPlaylistState
}
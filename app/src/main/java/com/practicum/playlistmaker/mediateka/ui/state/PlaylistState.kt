package com.practicum.playlistmaker.mediateka.ui.state

import com.practicum.playlistmaker.mediateka.domain.models.Playlist


sealed interface PlaylistState {

    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState

    data class Empty(
        val message: String
    ) : PlaylistState
}

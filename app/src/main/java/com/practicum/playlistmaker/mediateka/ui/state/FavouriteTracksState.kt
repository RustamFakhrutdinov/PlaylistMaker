package com.practicum.playlistmaker.mediateka.ui.state

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavouriteTracksState {

    data class Content(
        val tracks: List<Track>
    ) : FavouriteTracksState

    data class Empty(
        val message: String
    ) : FavouriteTracksState

}
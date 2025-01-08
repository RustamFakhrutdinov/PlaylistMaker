package com.practicum.playlistmaker.mediateka.domain.db

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    suspend fun addTrackToFavourite(track: Track)
    suspend fun deleteTrackFromFavourite(track: Track)

    suspend fun getFavouriteTracks(): Flow<List<Track>>

    suspend fun isFavourite(trackId: Int): Boolean

}
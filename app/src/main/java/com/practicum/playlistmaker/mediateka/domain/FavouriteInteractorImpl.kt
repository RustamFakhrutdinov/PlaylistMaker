package com.practicum.playlistmaker.mediateka.domain

import com.practicum.playlistmaker.mediateka.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavouriteInteractorImpl(
    private val favouriteRepository: FavouriteRepository
): FavouriteInteractor {
    override suspend fun addTrackToFavourite(track: Track) {
        favouriteRepository.addTrackToFavourite(track)
    }

    override suspend fun deleteTrackFromFavourite(track: Track) {
        favouriteRepository.deleteTrackFromFavourite(track)
    }

    override suspend fun getFavouriteTracks(): Flow<List<Track>> {
        return favouriteRepository.getFavouriteTracks()
    }

    override suspend fun isFavourite(trackId:Int): Boolean {
        return favouriteRepository.isFavourite(trackId)
    }
}
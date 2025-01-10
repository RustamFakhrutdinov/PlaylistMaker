package com.practicum.playlistmaker.mediateka.domain.db

import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylists() : Flow<List<Playlist>>
    suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int)
}
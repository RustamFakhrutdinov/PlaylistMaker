package com.practicum.playlistmaker.mediateka.domain.db

import android.net.Uri
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist, uri: Uri?)
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun getPlaylists() : Flow<List<Playlist>>
    suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int)

    suspend fun getTrackListFromPlaylist(playlistId: Int): Flow<List<Track>>
}
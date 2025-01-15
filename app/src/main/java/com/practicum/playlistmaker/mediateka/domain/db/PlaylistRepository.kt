package com.practicum.playlistmaker.mediateka.domain.db

import android.net.Uri
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    suspend fun addPlaylist(playlist: Playlist, uri: Uri?)
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int)
    suspend fun getTrackListFromPlaylist(playlistId: Int): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int)
    suspend fun getPlaylistCount(playlistId: Int): Int
    suspend fun getPlaylistById(playlistId: Int): Flow<Playlist?>?
    suspend fun editPlaylist(
        playlistId: Int,
        newName: String,
        newDescription: String?,
        newPath: Uri?
    )
}
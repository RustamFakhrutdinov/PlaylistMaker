package com.practicum.playlistmaker.mediateka.domain.impl

import android.net.Uri
import com.practicum.playlistmaker.di.repositoryModule
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
): PlaylistInteractor {
    override suspend fun addPlaylist(playlist: Playlist,uri: Uri?) {
        playlistRepository.addPlaylist(playlist, uri)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
       return playlistRepository.getPlaylists()
    }

    override suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int) {
        return playlistRepository.addTrackIdToPlaylist(track, playlistId)
    }

    override suspend fun getTrackListFromPlaylist(playlistId: Int): Flow<List<Track>> {
        return playlistRepository.getTrackListFromPlaylist(playlistId)
    }
}
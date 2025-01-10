package com.practicum.playlistmaker.mediateka.data.reposirtory

import com.practicum.playlistmaker.mediateka.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.dto.PlaylistDto
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter
) :PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistDto = PlaylistDto(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.path,
            playlist.trackIdList,
            playlist.count
        )
        playlistDao.insertPlaylist(playlistDbConverter.map(playlistDto))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistDto = PlaylistDto(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.path,
            playlist.trackIdList,
            playlist.count
        )
        playlistDao.deletePlaylist(playlistDbConverter.map(playlistDto))
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map {playlists ->
            convertFromPlaylistEntity(playlists)
        }
    }


    override suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int) {
        TODO("Not yet implemented")
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }

}
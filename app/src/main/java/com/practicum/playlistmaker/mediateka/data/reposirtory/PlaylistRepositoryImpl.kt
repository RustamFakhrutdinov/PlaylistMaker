package com.practicum.playlistmaker.mediateka.data.reposirtory

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediateka.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.data.converters.TrackInPlaylistsDbConverter
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackInPlaylistsDao
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.dto.PlaylistDto
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.history.HISTORY_TRACK_KEY
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val json: Gson,
    private val trackInPlaylistsDao: TrackInPlaylistsDao,
    private val trackInPlaylistsDbConverter: TrackInPlaylistsDbConverter
) :PlaylistRepository {
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
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
        playlistDao.addTrackIdToPlaylist(convertToString(track,playlistId), playlistId)
        playlistDao.countPlus(playlistId)
        val trackDto = TrackDto(
            track.trackId,
            track.trackName,
            track.artistName,
            parseTimeToMillis(track.trackTime),
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
        trackInPlaylistsDao.insertTrackInPlaylists(trackInPlaylistsDbConverter.map(trackDto))

    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }


   private fun readTrackListFromBD(playlistId: Int): ArrayList<Int> {

        val json: String? = playlistDao.getTrackIdList(playlistId)
       if (json.isNullOrEmpty()) {
           return arrayListOf()
       }
        val itemType = object : TypeToken<List<Int>>() {}.type

        return Gson().fromJson(json, itemType)
    }
    private fun convertToString(track: Track, playlistId: Int): String {

        var trackList = readTrackListFromBD(playlistId)
        trackList.add(track.trackId!!)
        return json.toJson(trackList)
    }

    private fun parseTimeToMillis(time: String): Long {
        val date = dateFormat.parse(time) ?: return 0L // Парсим строку в дату
        return date.time // Извлекаем миллисекунды
    }

}
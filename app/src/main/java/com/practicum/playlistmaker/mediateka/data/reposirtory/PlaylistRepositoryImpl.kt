package com.practicum.playlistmaker.mediateka.data.reposirtory

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediateka.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.data.converters.TrackInPlaylistsDbConverter
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackInPlaylistsDao
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackInPlaylistsEntity
import com.practicum.playlistmaker.mediateka.data.dto.PlaylistDto
import com.practicum.playlistmaker.mediateka.domain.db.PlaylistRepository
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date
import java.util.Locale

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val json: Gson,
    private val trackInPlaylistsDao: TrackInPlaylistsDao,
    private val trackInPlaylistsDbConverter: TrackInPlaylistsDbConverter,
    private val context: Context

) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist, uri: Uri?) {
        var pathUri: String? = null
        if (uri != null)
            pathUri = saveImageToPrivateStorage(uri!!)

        val playlistDto = PlaylistDto(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            pathUri,
            playlist.trackIdList,
            playlist.count
        )
        playlistDao.insertPlaylist(
            playlistDbConverter.map(playlistDto)
        )
    }

    override suspend fun deletePlaylist(playlistId: Int) {
        val trackIdList = readTrackListFromBD(playlistId)
        playlistDao.deletePlaylist(playlistId)
        for (trackId in trackIdList) {
            deleteTrackFromTrackInPlaylist(trackId)
        }

    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getPlaylists().map { playlists ->
            convertFromPlaylistEntity(playlists)
        }
    }


    override suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int) {
        playlistDao.addTrackIdsToPlaylist(convertToStringAdd(track, playlistId), playlistId)
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
        trackInPlaylistsDao.insertTrackInPlaylists(
            trackInPlaylistsDbConverter.map(
                trackDto,
                Instant.now().epochSecond
            )
        )
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Int) {
        playlistDao.addTrackIdsToPlaylist(convertToStringDelete(track, playlistId), playlistId)
        playlistDao.countMinus(playlistId)
        deleteTrackFromTrackInPlaylist(track.trackId!!)
    }

    override suspend fun getPlaylistCount(playlistId: Int): Int {
        return playlistDao.getCountOfPlaylist(playlistId)
    }

    override suspend fun getPlaylistById(playlistId: Int): Flow<Playlist?>? {
        return playlistDao.getPlaylistById(playlistId).map { playlist ->
            if (playlist != null) {
                playlistDbConverter.map(playlist)
            } else {
                null // Возвращайте null, если запись не найдена
            }
        }
    }

    override suspend fun editPlaylist(
        playlistId: Int,
        newName: String,
        newDescription: String?,
        newPath: Uri?
    ) {
        var pathUri: String? = null
        if (newPath != null)
            pathUri = saveImageToPrivateStorage(newPath!!)
        playlistDao.updatePlaylistDetails(playlistId, newName, newDescription, pathUri)
    }

    override suspend fun getTrackListFromPlaylist(playlistId: Int): Flow<List<Track>> {
        val trackIdList = readTrackListFromBD(playlistId)
        return trackInPlaylistsDao.getTracksByIds(trackIdList).map { tracks ->
            convertFromTracksEntity(tracks)
        }
    }

    private fun deleteTrackFromTrackInPlaylist(trackId: Int) {
        val playlistCount = playlistDao.getPlaylistsCount()

        val playlistList = playlistDao.getPlaylistsNotFlow()
        if (playlistCount != null) {
            var containsFlag: Boolean = false
            for (playlist in playlistList) {
                val trackList = readTrackListFromBD(playlist.playlistId)
                if (!trackList.isNullOrEmpty()) {
                    if (trackList.contains(trackId)) {
                        containsFlag = true
                        break
                    }
                }
            }
            if (!containsFlag) {
                trackInPlaylistsDao.deleteTrackById(trackId)
            }

        }
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }


    private fun convertFromTracksEntity(tracks: List<TrackInPlaylistsEntity>): List<Track> {
        return tracks.map { track ->
            trackInPlaylistsDbConverter.map(track)
        }
    }


    private fun readTrackListFromBD(playlistId: Int): ArrayList<Int> {

        val json: String? = playlistDao.getTrackIdList(playlistId)
        if (json.isNullOrEmpty()) {
            return arrayListOf()
        }
        val itemType = object : TypeToken<List<Int>>() {}.type

        return Gson().fromJson(json, itemType)
    }

    private fun convertToStringAdd(track: Track, playlistId: Int): String {
        var trackList = readTrackListFromBD(playlistId)
        trackList.add(track.trackId!!)
        return json.toJson(trackList)
    }

    private fun convertToStringDelete(track: Track, playlistId: Int): String {
        var trackList = readTrackListFromBD(playlistId)
        trackList.remove(track.trackId)
        return json.toJson(trackList)
    }

    private fun parseTimeToMillis(time: String): Long {
        val parts = time.split(":")
        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()
        return (minutes * 60 + seconds) * 1000L
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val contentResolver: ContentResolver = context.contentResolver
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlistmaker")


        // Создаем каталог, если он не существует
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val currentDate: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File(filePath, "playlist_image_$currentDate.jpg")

        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        return file.path
    }

}
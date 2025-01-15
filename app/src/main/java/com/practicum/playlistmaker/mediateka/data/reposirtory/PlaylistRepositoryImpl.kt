package com.practicum.playlistmaker.mediateka.data.reposirtory

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.R
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
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
        return playlistDao.getPlaylists().map { playlists ->
            convertFromPlaylistEntity(playlists)
        }
    }


    override suspend fun addTrackIdToPlaylist(track: Track, playlistId: Int) {
        playlistDao.addTrackIdToPlaylist(convertToString(track, playlistId), playlistId)
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

    override suspend fun getTrackListFromPlaylist(playlistId: Int): Flow<List<Track>> = flow {
        val trackIdList = readTrackListFromBD(playlistId)
        val trackList: MutableList<Track> = arrayListOf()
        if (!trackIdList.isNullOrEmpty()) {
            trackIdList.forEach { id ->
                var track = trackInPlaylistsDbConverter.map(trackInPlaylistsDao.getTrack(id)!!)
                trackList.add(track)
            }
        }
        emit(trackList)
    }


    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map { playlist -> playlistDbConverter.map(playlist) }
    }


    private suspend fun readTrackListFromBD(playlistId: Int): ArrayList<Int> {

        val json: String? = playlistDao.getTrackIdList(playlistId)
        if (json.isNullOrEmpty()) {
            return arrayListOf()
        }
        val itemType = object : TypeToken<List<Int>>() {}.type

        return Gson().fromJson(json, itemType)
    }

    private suspend fun convertToString(track: Track, playlistId: Int): String {

        var trackList = readTrackListFromBD(playlistId)
        trackList.add(track.trackId!!)
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


//    private fun saveImageToPrivateStorage(uri: Uri): String {
//        val contentResolver = requireActivity().applicationContext.contentResolver
//        //создаём экземпляр класса File, который указывает на нужный каталог
//        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.catalog))
//        //создаем каталог, если он не создан
//        if (!filePath.exists()){
//            filePath.mkdirs()
//        }
//        //создаём экземпляр класса File, который указывает на файл внутри каталога
//        val file = File(filePath, "playlist_image_$currentDate.jpg")
//        // передаём необходимый флаг на запись
//        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
//        contentResolver.takePersistableUriPermission(uri, takeFlags)
//        // создаём входящий поток байтов из выбранной картинки
//        val inputStream = contentResolver.openInputStream(uri)
//        // создаём исходящий поток байтов в созданный выше файл
//        val outputStream = FileOutputStream(file)
//        // записываем картинку с помощью BitmapFactory
//        BitmapFactory
//            .decodeStream(inputStream)
//            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
//        return file.path
//    }


}
package com.practicum.playlistmaker.mediateka.data

import com.practicum.playlistmaker.mediateka.data.converters.TrackFavouriteDbConverter
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackFavouriteDao
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteRepository
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

class FavouriteRepositoryImpl(
    private val trackFavouriteDao: TrackFavouriteDao,
    private val trackFavouriteDbConvertor: TrackFavouriteDbConverter,
): FavouriteRepository {

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }
    override suspend fun addTrackToFavourite(track: Track) {
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

        trackFavouriteDao.insertFavouriteTrack(trackFavouriteDbConvertor.map(trackDto, Instant.now().epochSecond))
    }

    override suspend fun deleteTrackFromFavourite(track: Track) {
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
        trackFavouriteDao.deleteFavouriteTrack(trackFavouriteDbConvertor.map(trackDto,Instant.now().epochSecond))
    }

    override suspend fun getFavouriteTracks(): Flow<List<Track>> {

        return trackFavouriteDao.getFavouriteTracks().map { tracks ->
            convertFromTracksEntity(tracks)
        }
    }

    override suspend fun isFavourite(trackId: Int): Boolean {
        val tracksIds = trackFavouriteDao.getFavouriteTracksId()
        return trackId in tracksIds
    }

    private fun convertFromTracksEntity(tracks: List<TrackFavouriteEntity>): List<Track> {
        return tracks.map { track -> trackFavouriteDbConvertor.map(track) }
    }

    private fun parseTimeToMillis(time: String): Long {
        val date = dateFormat.parse(time) ?: return 0L // Парсим строку в дату
        return date.time // Извлекаем миллисекунды
    }

}
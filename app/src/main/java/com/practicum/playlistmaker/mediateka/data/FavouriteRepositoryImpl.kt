package com.practicum.playlistmaker.mediateka.data

import com.practicum.playlistmaker.mediateka.data.converters.TrackFavouriteDbConverter
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteRepository
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Locale

class FavouriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackFavouriteDbConvertor: TrackFavouriteDbConverter,
): FavouriteRepository {
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
        appDatabase.trackFavouriteDao().insertFavouriteTrack(trackFavouriteDbConvertor.map(trackDto))
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
        appDatabase.trackFavouriteDao().deleteFavouriteTrack(trackFavouriteDbConvertor.map(trackDto))
    }

//    override suspend fun getFavouriteTracks(): Flow<List<Track>> = flow {
//        val tracks = appDatabase.trackFavouriteDao().getFavouriteTracks()
//        emit(convertFromTracksEntity(tracks))
//    }

    override suspend fun getFavouriteTracks(): Flow<List<Track>> {
        return appDatabase.trackFavouriteDao()
            .getFavouriteTracks()
            .map { tracks -> convertFromTracksEntity(tracks) }
    }

    override suspend fun isFavourite(trackId: Int): Boolean {
        val tracksIds = appDatabase.trackFavouriteDao().getFavouriteTracksId()
        return trackId in tracksIds
    }

    private fun convertFromTracksEntity(tracks: List<TrackFavouriteEntity>): List<Track> {
        return tracks.map { track -> trackFavouriteDbConvertor.map(track) }
    }

    private fun parseTimeToMillis(time: String): Long {
        val sdf = SimpleDateFormat("mm:ss", Locale.getDefault())
        val date = sdf.parse(time) ?: return 0L // Парсим строку в дату
        return date.time // Извлекаем миллисекунды
    }

}
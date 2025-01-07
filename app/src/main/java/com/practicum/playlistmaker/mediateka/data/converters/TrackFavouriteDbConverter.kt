package com.practicum.playlistmaker.mediateka.data.converters

import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Locale

class TrackFavouriteDbConverter {
    fun map(track: TrackDto): TrackFavouriteEntity {
        return TrackFavouriteEntity(track.trackId!!,
            track.trackName?: "",
            track.artistName?: "",
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis?:0L),
            track.artworkUrl100?: "",
            track.collectionName?: "",
            track.releaseDate?: "",
            track.primaryGenreName?: "",
            track.country?: "",
            track.previewUrl?: "",
            Instant.now().epochSecond
        )
    }

    fun map(track: TrackFavouriteEntity): Track {

        return Track(track.trackId,track.trackName,track.artistName, track.trackTime,
            track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName,
            track.country, track.previewUrl)
    }
}
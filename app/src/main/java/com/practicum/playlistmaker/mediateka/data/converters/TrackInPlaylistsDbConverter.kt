package com.practicum.playlistmaker.mediateka.data.converters

import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackInPlaylistsEntity
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackInPlaylistsDbConverter {
    fun map(track: TrackDto): TrackInPlaylistsEntity {
        return TrackInPlaylistsEntity(track.trackId!!,
            track.trackName?: "",
            track.artistName?: "",
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis?:0L),
            track.artworkUrl100?: "",
            track.collectionName?: "",
            track.releaseDate?: "",
            track.primaryGenreName?: "",
            track.country?: "",
            track.previewUrl?: "",
        )
    }

    fun map(track: TrackInPlaylistsEntity): Track {

        return Track(track.trackId,track.trackName,track.artistName, track.trackTime,
            track.artworkUrl100, track.collectionName, track.releaseDate, track.primaryGenreName,
            track.country, track.previewUrl)
    }
}
package com.practicum.playlistmaker.mediateka.data.converters

import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.mediateka.data.dto.PlaylistDto
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistDbConverter {
    fun map(playlist: PlaylistDto): PlaylistEntity {
        return PlaylistEntity(
            playlist.playlistId?:0,
            playlist.name?:"",
            playlist.description,
            playlist.path,
            playlist.trackIdList,
            playlist.count?:0
        )
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.playlistId,
            playlist.name,
            playlist.description,
            playlist.path,
            playlist.trackIdList,
            playlist.count
        )
    }
}
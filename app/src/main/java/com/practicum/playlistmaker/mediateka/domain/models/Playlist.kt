package com.practicum.playlistmaker.mediateka.domain.models

data class Playlist(
    val playlistId: Int,
    var name: String,
    var description: String?,
    var path: String?,
    var trackIdList: String?,
    var count: Int
)

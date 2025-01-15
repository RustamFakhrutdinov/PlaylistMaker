package com.practicum.playlistmaker.mediateka.data.dto

data class PlaylistDto(
    val playlistId: Int? = null,
    var name: String? = null,
    var description: String? = null,
    var path: String? = null,
    var trackIdList: String? = null,
    var count: Int? = null
)

package com.practicum.playlistmaker.data.dto

class TracksResponse(val resultCount: Int,
                     val results: List<TrackDto>): Response()
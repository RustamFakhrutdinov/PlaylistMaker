package com.practicum.playlistmaker.search.data.dto

class TracksResponse(val resultCount: Int,
                     val results: List<TrackDto>): Response()
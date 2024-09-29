package com.practicum.playlistmaker.data.network

import com.practicum.playlistmaker.data.NetworkClient
import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    private var resultResponseCode: Int = 0

    override fun search(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        resultResponseCode = response.resultCode
        if (response.resultCode == 200) {
            return (response as TracksResponse).results.map {
                Track(
                    it.trackName?:"No track name",
                    it.artistName?: "No artist name",
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis?:0L),
                    it.artworkUrl100?:"No artworkUrl",
                    it.trackId?:0,
                    it.collectionName?:"No collection name",
                    it.releaseDate?:"No release date",
                    it.primaryGenreName?:"No primary genre name",
                    it.country?:"No country",
                    it.previewUrl?:"No previewUrl")
            }
        } else {
            resultResponseCode = response.resultCode
            return emptyList()
        }
    }

    override fun getResultCode():Int {
        return resultResponseCode
    }
}
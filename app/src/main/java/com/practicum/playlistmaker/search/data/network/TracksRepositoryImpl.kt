package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {
    override fun search(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                Resource.Success((response as TracksResponse).results.map {
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
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }
}
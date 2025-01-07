package com.practicum.playlistmaker.search.data.network

import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val appDatabase: AppDatabase
) : TracksRepository {
    override fun search(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                val favouriteTracks = appDatabase.trackFavouriteDao().getFavouriteTracksId()
                emit(Resource.Success((response as TracksResponse).results.map {
                    Track(
                        it.trackId?:-1,
                        it.trackName?:"No track name",
                        it.artistName?: "No artist name",
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.trackTimeMillis?:0L),
                        it.artworkUrl100?:"No artworkUrl",
                        it.collectionName?:"No collection name",
                        it.releaseDate?:"No release date",
                        it.primaryGenreName?:"No primary genre name",
                        it.country?:"No country",
                        it.previewUrl?:"No previewUrl",
                        it.trackId in favouriteTracks
                    )
                }))
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}
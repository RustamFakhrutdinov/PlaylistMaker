package com.practicum.playlistmaker.mediateka.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.domain.db.FavouriteInteractor
import com.practicum.playlistmaker.mediateka.ui.state.FavouriteTracksState
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favouriteInteractor: FavouriteInteractor,
    private val context: Context
): ViewModel() {
    init {
        //fillData()
    }
    private val favoritesLiveData = MutableLiveData<FavouriteTracksState>()
    fun getFavoritesLiveData(): LiveData<FavouriteTracksState> = favoritesLiveData

    fun fillData() {
        viewModelScope.launch(Dispatchers.IO) {
            favouriteInteractor
                .getFavouriteTracks()
                .collect{ tracks ->
                    processResult(tracks)
                }
        }
    }
    private fun processResult(tracks: List<Track>) {
        if (tracks.isEmpty()) {
            renderState(FavouriteTracksState.Empty(context.getString(R.string.favorites_tracks_empty)))
        } else {
            renderState(FavouriteTracksState.Content(tracks))
        }
    }

    private fun renderState(state: FavouriteTracksState) {
        favoritesLiveData.postValue(state)
    }
}
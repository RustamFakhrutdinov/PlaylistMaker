package com.practicum.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import java.text.FieldPosition

class PlayListViewModel(private val playlistNumber: Int) : ViewModel() {
    private val playlistLiveData = MutableLiveData(playlistNumber)
    fun getPlaylistLiveData(): LiveData<Int> = playlistLiveData
}
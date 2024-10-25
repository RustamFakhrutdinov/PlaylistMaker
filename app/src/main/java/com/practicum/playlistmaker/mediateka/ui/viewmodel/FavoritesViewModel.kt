package com.practicum.playlistmaker.mediateka.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class FavoritesViewModel(private val position: Int): ViewModel() {
    private val favoritesLiveData = MutableLiveData(position)
    fun getFavoritesLiveData(): LiveData<Int> = favoritesLiveData
}
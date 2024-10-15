package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.PlayerInteractor
import com.practicum.playlistmaker.player.domain.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(private val repository: PlayerRepository): PlayerInteractor {
    override fun preparePlayer() {
        repository.preparePlayer()
    }

    override fun getTime(): String {
       return repository.getTime()
    }
    override fun loadTrackData(): Track {
       return repository.loadTrackData()
    }
    override fun play(status: PlayerInteractor.StatusObserver) {
        repository.play(status)
    }
    override fun pause() {
        repository.pause()
    }
    override fun release() {
        repository.release()
    }
}
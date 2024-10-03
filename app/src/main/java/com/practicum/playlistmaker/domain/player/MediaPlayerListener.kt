package com.practicum.playlistmaker.domain.player

interface MediaPlayerListener {
    fun onPlayerCompletion()

    fun onPlayerStart()

    fun onPlayerPause()
}
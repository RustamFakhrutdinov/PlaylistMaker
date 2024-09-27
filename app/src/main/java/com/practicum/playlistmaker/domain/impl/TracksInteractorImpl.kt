package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun search(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(repository.search(expression))
        }
    }

}
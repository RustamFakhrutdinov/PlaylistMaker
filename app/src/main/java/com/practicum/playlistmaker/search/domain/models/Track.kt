package com.practicum.playlistmaker.search.domain.models

data class Track(val trackName: String,         // Название композиции
                 val artistName: String,        // Имя исполнителя
                 val trackTime: String,         // Продолжительность трека(преобразованная)
                 val artworkUrl100: String,     // Ссылка на изображение обложки
                 val trackId: Int,              // id трека
                 val collectionName: String,    // Название альбома
                 val releaseDate: String,       // дата выхода
                 val primaryGenreName: String,  // жанр
                 val country: String,           // страна
                 val previewUrl:String          // ссылка на отрывок трека
) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}

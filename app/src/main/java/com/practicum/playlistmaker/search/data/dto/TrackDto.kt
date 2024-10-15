package com.practicum.playlistmaker.search.data.dto



data class TrackDto(val trackName: String? = null,          // Название композиции
                    val artistName: String? = null,         // Имя исполнителя
                    val trackTimeMillis: Long? = null,      // Продолжительность трека
                    val artworkUrl100: String? = null,      // Ссылка на изображение обложки
                    val trackId: Int? = null,               // id трека
                    val collectionName: String? = null,     // Название альбома
                    val releaseDate: String? = null,        // дата выхода
                    val primaryGenreName: String? = null,   // жанр
                    val country: String? = null,            // страна
                    val previewUrl:String? = null           // ссылка на отрывок трека
) {
    fun getCoverArtwork() = artworkUrl100?.replaceAfterLast('/',"512x512bb.jpg")
}
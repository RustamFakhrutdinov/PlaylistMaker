package com.practicum.playlistmaker

data class Track(val trackName: String, // Название композиции
                 val artistName: String, // Имя исполнителя
                 val trackTimeMillis: Long, // Продолжительность трека
                 val artworkUrl100: String, // Ссылка на изображение обложки
                 val trackId: Int, // id трека
                 val collectionName: String,// Название альбома
                 val releaseDate: String, //дата выхода
                 val primaryGenreName: String,// жанр
                 val country: String// страна

) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}

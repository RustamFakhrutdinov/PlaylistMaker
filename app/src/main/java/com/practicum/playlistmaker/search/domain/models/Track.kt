package com.practicum.playlistmaker.search.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Track(val trackId: Int? = null,      // id трека
                 val trackName: String,         // Название композиции
                 val artistName: String,        // Имя исполнителя
                 val trackTime: String,         // Продолжительность трека(преобразованная)
                 val artworkUrl100: String,     // Ссылка на изображение обложки
                 val collectionName: String,    // Название альбома
                 val releaseDate: String,       // дата выхода
                 val primaryGenreName: String,  // жанр
                 val country: String,           // страна
                 val previewUrl:String,          // ссылка на отрывок трека
                 var isFavorite: Boolean = false // добавлен ли трек в избранное
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
}

package com.practicum.playlistmaker.mediateka.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favourite_tracks_table")
data class TrackFavouriteEntity(
    @PrimaryKey
    val trackId: Int,              // id трека
    val trackName: String,         // Название композиции
    val artistName: String,        // Имя исполнителя
    val trackTime: String,         // Продолжительность трека(преобразованная)
    val artworkUrl100: String,     // Ссылка на изображение обложки
    val collectionName: String,    // Название альбома
    val releaseDate: String,       // дата выхода
    val primaryGenreName: String,  // жанр
    val country: String,           // страна
    val previewUrl:String,          // ссылка на отрывок трека
    val addedTimestamp: Long       // время добавления трека
)
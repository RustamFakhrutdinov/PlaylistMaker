package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackFavouriteDao
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackInPlaylistsDao
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackInPlaylistsEntity


@Database(version = 3, entities = [TrackFavouriteEntity::class, PlaylistEntity::class, TrackInPlaylistsEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackFavouriteDao(): TrackFavouriteDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun trackInPlaylistsDao(): TrackInPlaylistsDao
}
package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackFavouriteDao
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity


@Database(version = 1, entities = [TrackFavouriteEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackFavouriteDao(): TrackFavouriteDao
}
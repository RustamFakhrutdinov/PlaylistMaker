package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.mediateka.data.db.dao.TrackFavouriteDao
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity


//@Database(version = 2, entities = [TrackFavouriteEntity::class, PlaylistEntity::class])
//abstract class AppDatabase: RoomDatabase() {
//    abstract fun trackFavouriteDao(): TrackFavouriteDao
//    abstract fun playlistDao(): PlaylistDao
//}
@Database(version = 1, entities = [TrackFavouriteEntity::class])
abstract class AppDatabase: RoomDatabase() {
    abstract fun trackFavouriteDao(): TrackFavouriteDao
}
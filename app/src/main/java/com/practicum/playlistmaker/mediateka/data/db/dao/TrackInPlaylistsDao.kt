package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackInPlaylistsEntity
@Dao
interface TrackInPlaylistsDao {
    @Insert(entity = TrackInPlaylistsEntity::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylists(track: TrackInPlaylistsEntity)
}
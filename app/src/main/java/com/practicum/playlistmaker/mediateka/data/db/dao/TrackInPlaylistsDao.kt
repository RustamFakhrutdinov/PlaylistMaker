package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackInPlaylistsEntity
@Dao
interface TrackInPlaylistsDao {
    @Insert(entity = TrackInPlaylistsEntity::class,onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylists(track: TrackInPlaylistsEntity)

    @Query("SELECT * FROM track_in_playlists_table WHERE trackId = (:id)")
    suspend fun getTrack(id: Int): TrackInPlaylistsEntity?
}
package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackInPlaylistsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackInPlaylistsDao {
    @Insert(entity = TrackInPlaylistsEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrackInPlaylists(track: TrackInPlaylistsEntity)

    @Delete(entity = TrackInPlaylistsEntity::class)
    suspend fun deleteTrackFromPlaylists(track: TrackInPlaylistsEntity)

    @Query("SELECT * FROM track_in_playlists_table WHERE trackId = (:id)")
    fun getTracks(id: Int): Flow<List<TrackInPlaylistsEntity>>

    @Query("SELECT * FROM track_in_playlists_table WHERE trackId IN (:trackIds) ORDER BY addedTimestamp DESC")
    fun getTracksByIds(trackIds: List<Int>): Flow<List<TrackInPlaylistsEntity>>

    @Query("DELETE FROM track_in_playlists_table WHERE trackId = :trackId")
    fun deleteTrackById(trackId: Int)

}
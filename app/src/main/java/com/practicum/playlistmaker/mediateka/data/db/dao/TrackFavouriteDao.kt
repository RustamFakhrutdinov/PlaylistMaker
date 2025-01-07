package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackFavouriteDao {
    @Insert(entity = TrackFavouriteEntity::class,onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteTrack(track: TrackFavouriteEntity)

    @Delete(entity = TrackFavouriteEntity::class)
    suspend fun deleteFavouriteTrack(track: TrackFavouriteEntity)

//    @Query("SELECT * FROM favourite_tracks_table ORDER BY addedTimestamp DESC")
//    suspend fun getFavouriteTracks(): List<TrackFavouriteEntity>
    @Query("SELECT * FROM favourite_tracks_table ORDER BY addedTimestamp DESC")
    fun getFavouriteTracks(): Flow<List<TrackFavouriteEntity>>

    @Query("SELECT trackId FROM favourite_tracks_table")
    suspend fun getFavouriteTracksId(): List<Int>
}
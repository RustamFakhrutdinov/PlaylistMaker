package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.mediateka.data.db.entity.TrackFavouriteEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    fun deletePlaylist(playlist: PlaylistEntity)

    //получение всех плейлистов
    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    //получение списка треков Id плейлиста
    @Query("SELECT trackIdList FROM playlist_table WHERE playlistId = (:id)")
    fun getTrackIdList(id: Int): String?

    // обновить треки в плейлисте
    @Query("UPDATE playlist_table SET trackIdList = :newTrackIdList WHERE playlistId = :identificator")
    fun setTrackIdListByPlaylistId(newTrackIdList: String, identificator: Int)

    //обновить значение count в плейлисте
    @Query("UPDATE playlist_table SET count = count+1 WHERE playlistId = :identificator")
    fun countPlusOne(identificator: Int)

}
package com.practicum.playlistmaker.mediateka.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.mediateka.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(entity = PlaylistEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertPlaylist(playlist: PlaylistEntity)

    @Delete(entity = PlaylistEntity::class)
    fun deletePlaylist(playlist: PlaylistEntity)

    @Query("DELETE FROM playlist_table WHERE playlistId =:playlistId")
    fun deletePlaylist(playlistId: Int)

    //получение всех плейлистов
    @Query("SELECT * FROM playlist_table")
    fun getPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlist_table")
    fun getPlaylistsNotFlow(): List<PlaylistEntity>

    // получение плейлиста по id
    @Query("SELECT * FROM playlist_table WHERE playlistId = (:id)")
    fun getPlaylistById(id: Int): Flow<PlaylistEntity>

    //получение списка треков Id плейлиста
    @Query("SELECT trackIdList FROM playlist_table WHERE playlistId = (:id)")
    fun getTrackIdList(id: Int): String?

    @Query("SELECT count FROM playlist_table WHERE playlistId = (:id)")
    fun getCountOfPlaylist(id: Int): Int

    // обновить треки в плейлисте
    @Query("UPDATE playlist_table SET trackIdList = :newTrackIdList WHERE playlistId = :id")
    fun addTrackIdsToPlaylist(newTrackIdList: String, id: Int)

    //добавить значение count в плейлисте на 1
    @Query("UPDATE playlist_table SET count = count+1 WHERE playlistId = :id")
    fun countPlus(id: Int)

    @Query("UPDATE playlist_table SET count = count - 1 WHERE playlistId = :id")
    fun countMinus(id: Int)

    @Query("UPDATE playlist_table SET count = :count WHERE playlistId = :id")
    fun setCount(count: Int, id: Int)

    //получение количество плейлистов
    @Query("SELECT COUNT(*) FROM playlist_table")
    fun getPlaylistsCount(): Int

    @Query("""
        UPDATE playlist_table
        SET name = :newName,
            description = :newDescription,
            path = CASE 
                WHEN :newPath IS NOT NULL THEN :newPath
                ELSE path
            END
        WHERE playlistId = :playlistId
    """)
    fun updatePlaylistDetails(
        playlistId: Int,
        newName: String,
        newDescription: String?,
        newPath: String?
    )

}
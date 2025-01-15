package com.practicum.playlistmaker.mediateka.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "playlist_table")
data class PlaylistEntity (
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    var name: String,
    var description: String?,
    var path: String?,
    var trackIdList: String?,
    var count: Int
)
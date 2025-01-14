package com.practicum.playlistmaker.mediateka.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    val playlistId: Int,
    var name: String,
    var description: String?,
    var path: String?,
    var trackIdList: String?,
    var count: Int
): Parcelable

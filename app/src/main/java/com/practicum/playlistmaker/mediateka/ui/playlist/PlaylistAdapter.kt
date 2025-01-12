package com.practicum.playlistmaker.mediateka.ui.playlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder

class PlaylistAdapter(private val playlists: List<Playlist>?): RecyclerView.Adapter<PlaylistViewHolder>() {
    var onPlaylistClickListener: PlaylistViewHolder.OnPlaylistClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(PlaylistItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists?.size?:0
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists!![position])
        holder.onPlaylistClickListener = onPlaylistClickListener
    }

}
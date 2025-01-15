package com.practicum.playlistmaker.player.ui.playlist_bottom_sheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.BottomSheetPlaylistItemBinding
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
class BottomSheeetPlaylistAdapter (private val playlists: List<Playlist>?): RecyclerView.Adapter<BottomSheetPlaylistViewHolder>() {
    var onPlaylistClickListener: BottomSheetPlaylistViewHolder.OnPlaylistClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetPlaylistViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return BottomSheetPlaylistViewHolder(BottomSheetPlaylistItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun getItemCount(): Int {
        return playlists?.size?:0
    }

    override fun onBindViewHolder(holder: BottomSheetPlaylistViewHolder, position: Int) {
        holder.bind(playlists!![position])
        holder.onPlaylistClickListener = onPlaylistClickListener
    }

}
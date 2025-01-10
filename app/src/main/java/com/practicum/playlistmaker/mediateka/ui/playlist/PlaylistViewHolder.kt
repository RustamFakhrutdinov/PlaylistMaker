package com.practicum.playlistmaker.mediateka.ui.playlist

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistItemBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist


class PlaylistViewHolder(private val binding: PlaylistItemBinding): RecyclerView.ViewHolder(binding.root) {
    private val image: ImageView = binding.cover
    private val playlistName: TextView = binding.playlistName
    private val playlistCount: TextView = binding.playlistCount
    var onTrackClickListener: OnPlaylistClickListener? = null
    fun bind(playlist: Playlist) {
        Glide.with(itemView)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(0f,itemView.context)))
            .into(image)
        playlistName.text = playlist.name
       // playlistCount.text = playlist.count
        itemView.setOnClickListener {
//            onTrackClickListener?.onTrackClick(track)
        }
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
    fun interface OnPlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}
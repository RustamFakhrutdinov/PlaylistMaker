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
    var onPlaylistClickListener: OnPlaylistClickListener? = null
    fun bind(playlist: Playlist) {
        Glide.with(itemView)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder)
            .into(image)
        playlistName.text = playlist.name
        playlistCount.text = playlist.count.toString() + " трек" + getTrackWordEnding(playlist.count)
        itemView.setOnClickListener {
            onPlaylistClickListener?.onPlaylistClick(playlist)
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

    private fun getTrackWordEnding(count: Int): String {
        return when {
            count % 100 in 11..19 -> "ов" // Числа от 11 до 19
            count % 10 == 1 -> ""         // Оканчивается на 1, кроме 11
            count % 10 in 2..4 -> "а"    // Оканчивается на 2, 3, 4, кроме 12-14
            else -> "ов"                 // Все остальные случаи
        }
    }
}
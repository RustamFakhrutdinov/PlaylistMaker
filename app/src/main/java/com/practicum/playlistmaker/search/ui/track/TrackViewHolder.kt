package com.practicum.playlistmaker.search.ui.track

import android.content.Context
import android.util.TypedValue
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackViewHolder (private val binding: TrackItemBinding): RecyclerView.ViewHolder(binding.root) {
    private val trackName: TextView = binding.trackName
    private val performerName: TextView = binding.performerName
    private val time: TextView = binding.trackTime
    private val trackCover: ImageView = binding.trackCover
    var onTrackClickListener: OnTrackClickListener? = null
    var onTrackLongClickListener: OnTrackLongClickListener? = null
    fun bind(track: Track) {
        val imageUrl:String = track.artworkUrl100
        performerName.requestLayout()
        trackName.text = track.trackName
        performerName.text = track.artistName
        time.text = track.trackTime
        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f,itemView.context)))
            .into(trackCover)
        itemView.setOnClickListener {
            onTrackClickListener?.onTrackClick(track)
        }
        itemView.setOnLongClickListener {
            onTrackLongClickListener?.onTrackLongClick(track)
            true
        }
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
    fun interface OnTrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface OnTrackLongClickListener {
        fun onTrackLongClick(track: Track)
    }

}

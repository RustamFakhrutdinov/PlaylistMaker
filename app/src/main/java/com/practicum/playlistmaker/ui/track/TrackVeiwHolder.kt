package com.practicum.playlistmaker.ui.track

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

class TrackVeiwHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val performerName: TextView = itemView.findViewById(R.id.performerName)
    private val time: TextView = itemView.findViewById(R.id.trackTime)
    private val trackCover: ImageView = itemView.findViewById(R.id.trackСover)
    var onTrackClickListener: OnTrackClickListener? = null
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

}


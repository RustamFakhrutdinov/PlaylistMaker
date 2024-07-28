package com.practicum.playlistmaker

import android.app.Application
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (private val tracks: ArrayList<Track>?) : RecyclerView.Adapter<TrackVeiwHolder> () {
    var onTrackClickListener: TrackVeiwHolder.OnTrackClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVeiwHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackVeiwHolder(view)
    }

    override fun onBindViewHolder(holder: TrackVeiwHolder, position: Int) {
        holder.bind(tracks!![position])
        holder.onTrackClickListener = onTrackClickListener
    }

    override fun getItemCount(): Int {
        return tracks?.size ?: 0
    }

    fun onTrackClick(track: Track) {
        
    }

}
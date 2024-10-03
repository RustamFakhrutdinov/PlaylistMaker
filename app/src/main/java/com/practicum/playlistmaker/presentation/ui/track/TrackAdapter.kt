package com.practicum.playlistmaker.presentation.ui.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

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


}
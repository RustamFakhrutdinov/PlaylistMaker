package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter (
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackVeiwHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackVeiwHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return TrackVeiwHolder(view)
    }

    override fun onBindViewHolder(holder: TrackVeiwHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}
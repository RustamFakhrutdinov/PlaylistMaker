package com.practicum.playlistmaker.search.ui.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter (private val tracks: ArrayList<Track>?) : RecyclerView.Adapter<TrackViewHolder> () {
    var onTrackClickListener: TrackViewHolder.OnTrackClickListener? = null
    var onTrackLongClickListener: TrackViewHolder.OnTrackLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks!![position])
        holder.onTrackClickListener = onTrackClickListener
        holder.onTrackLongClickListener = onTrackLongClickListener
    }

    override fun getItemCount(): Int {
        return tracks?.size ?: 0
    }


}
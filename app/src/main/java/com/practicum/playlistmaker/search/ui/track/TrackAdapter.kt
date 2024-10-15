package com.practicum.playlistmaker.search.ui.track

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackItemBinding
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter (private val tracks: ArrayList<Track>?) : RecyclerView.Adapter<TrackViewHolder> () {
    var onTrackClickListener: TrackViewHolder.OnTrackClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val layoutInspector = LayoutInflater.from(parent.context)
        return TrackViewHolder(TrackItemBinding.inflate(layoutInspector, parent, false))
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks!![position])
        holder.onTrackClickListener = onTrackClickListener
    }

    override fun getItemCount(): Int {
        return tracks?.size ?: 0
    }


}






//package com.practicum.playlistmaker.presentation.ui.track
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.practicum.playlistmaker.R
//import com.practicum.playlistmaker.search.domain.models.Track
//
//
////import com.practicum.playlistmaker.domain.models.Track
//
//class TrackAdapter (private val tracks: ArrayList<Track>?) : RecyclerView.Adapter<TrackViewHolder> () {
//    var onTrackClickListener: TrackViewHolder.OnTrackClickListener? = null
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
//        return TrackViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
//        holder.bind(tracks!![position])
//        holder.onTrackClickListener = onTrackClickListener
//    }
//
//    override fun getItemCount(): Int {
//        return tracks?.size ?: 0
//    }
//
//
//}
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

//package com.practicum.playlistmaker.presentation.ui.track
//
//import android.content.Context
//import android.util.TypedValue
//import android.view.View
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.practicum.playlistmaker.R
//import com.practicum.playlistmaker.search.domain.models.Track
//
//class TrackViewHolder (itemView: View): RecyclerView.ViewHolder(itemView) {
//
//    private val trackName: TextView = itemView.findViewById(R.id.trackName)
//    private val performerName: TextView = itemView.findViewById(R.id.performerName)
//    private val time: TextView = itemView.findViewById(R.id.trackTime)
//    private val trackCover: ImageView = itemView.findViewById(R.id.trackCover)
//    var onTrackClickListener: OnTrackClickListener? = null
//    fun bind(track: Track) {
//        val imageUrl:String = track.artworkUrl100
//        performerName.requestLayout()
//        trackName.text = track.trackName
//        performerName.text = track.artistName
//        time.text = track.trackTime
//        Glide.with(itemView)
//            .load(imageUrl)
//            .placeholder(R.drawable.placeholder)
//            .centerCrop()
//            .transform(RoundedCorners(dpToPx(2f,itemView.context)))
//            .into(trackCover)
//        itemView.setOnClickListener {
//            onTrackClickListener?.onTrackClick(track)
//        }
//    }
//    fun dpToPx(dp: Float, context: Context): Int {
//        return TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP,
//            dp,
//            context.resources.displayMetrics).toInt()
//    }
//    fun interface OnTrackClickListener {
//        fun onTrackClick(track: Track)
//    }
//
//}
package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackVeiwHolder (itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val performerName: TextView = itemView.findViewById(R.id.performerName)
    private val time: TextView = itemView.findViewById(R.id.trackTime)
    private val trackCover: ImageView = itemView.findViewById(R.id.trackСover)

    fun bind(track: Track) {
        val imageUrl:String = track.artworkUrl100

        if(track.trackName.length > 30) {
            trackName.text = track.trackName.substring(0,30) + "..."
        } else {
            trackName.text = track.trackName
        }

        if(track.artistName.length > 30) {
            performerName.text = track.artistName.substring(0,30) + "..."
        } else {
            performerName.text = track.artistName
        }

        time.text = track.trackTime
        Glide.with(itemView)
            .load(imageUrl)
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2f,itemView.context)))
            .into(trackCover)
    }
    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

}


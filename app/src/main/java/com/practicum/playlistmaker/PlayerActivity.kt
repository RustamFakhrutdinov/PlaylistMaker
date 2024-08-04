package com.practicum.playlistmaker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.internal.ViewUtils.dpToPx
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity: AppCompatActivity() {

    private lateinit var sharedPrefs : SharedPreferences

    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackPerformer: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView

    private lateinit var albumGroup: Group
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        sharedPrefs = getSharedPreferences(HISTORY_TRACK_PREFERENCES, MODE_PRIVATE)

        val backButton = findViewById<ImageButton>(R.id.backButtonPlayer)
        cover = findViewById<ImageView>(R.id.cover)
        trackName = findViewById<TextView>(R.id.trackName)
        trackPerformer = findViewById<TextView>(R.id.performerName)
        trackDuration = findViewById<TextView>(R.id.trackDuration)
        trackYear = findViewById<TextView>(R.id.trackYear)
        trackAlbum = findViewById<TextView>(R.id.trackAlbum)
        trackGenre = findViewById<TextView>(R.id.trackGenre)
        trackCountry = findViewById<TextView>(R.id.trackCountry)

        albumGroup = findViewById<Group>(R.id.albumGroup)



        addInformation(sharedPrefs)

        backButton.setOnClickListener {
            finish()
        }



    }

    private fun addInformation(sharedPreferences: SharedPreferences) {
        val tracksList = SearchHistory(sharedPrefs).read()
        val track = tracksList[0]

        trackName.text = track.trackName
        trackPerformer.text = track.artistName
        trackDuration.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        if (track.collectionName != null) {
            trackAlbum.text = track.collectionName
        } else {
            albumGroup.visibility = View.GONE
        }

        trackYear.text = track.releaseDate.substring(0,3)
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f,applicationContext)))
            .into(cover)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }
}
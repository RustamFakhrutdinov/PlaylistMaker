package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity: AppCompatActivity() {
    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackPerformer: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var trackTime: TextView
    private lateinit var playButton: ImageButton

    private lateinit var albumGroup: Group

    private lateinit var track: Track

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel: PlayerViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.backButtonPlayer

        track = viewModel.loadTrackData()
        initializeViews()
        addInformation()

        viewModel.getPlayStatusLiveData().observe(this) { playStatus ->
            playButtonChange(playStatus)
            trackTime.text = playStatus.progress
        }
        playButton.setOnClickListener {
            val currentPlayStatus = viewModel.getPlayStatusLiveData().value
            if (currentPlayStatus?.isPlaying == true) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
        }

        backButton.setOnClickListener {
            finish()
        }




    }

    private fun playButtonChange(playStatus: PlayStatus) {
        if(playStatus.isPlaying) {
            playButton.setImageResource(R.drawable.pause)
        } else {
            playButton.setImageResource(R.drawable.play)
        }

    }

    private fun initializeViews() {
        cover = binding.cover
        trackName = binding.trackName
        trackPerformer = binding.performerName
        trackDuration = binding.trackDuration
        trackYear = binding.trackYear
        trackAlbum = binding.trackAlbum
        trackGenre = binding.trackGenre
        trackCountry = binding.trackCountry
        playButton = binding.playButton
        trackTime = binding.trackTime
        albumGroup = binding.albumGroup
    }

    private fun addInformation() {
        trackName.text = track.trackName
        trackPerformer.text = track.artistName
        trackDuration.text = track.trackTime
        if (track.collectionName != null) {
            trackAlbum.text = track.collectionName
        } else {
            albumGroup.visibility = View.GONE
        }

        trackYear.text = track.releaseDate.substring(0,4)
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
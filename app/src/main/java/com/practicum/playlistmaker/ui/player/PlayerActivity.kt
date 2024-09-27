package com.practicum.playlistmaker.ui.player

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

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
    private lateinit var updateTimerTask: Runnable
    private lateinit var mediaPlayer: MediaPlayer

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val TIMER_DELAY = 500L

        // Число миллисекунд в одной секунде
        private const val DELAY = 1000L
    }

    private var mainThreadHandler: Handler? = null

    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        mainThreadHandler = Handler(Looper.getMainLooper())
        updateTimerTask = createUpdateTimerTask()

        val backButton = findViewById<ImageButton>(R.id.backButtonPlayer)
        track = Creator.provideSearchHistoryInteractor(this).readFromHistory()[0]
        //track = SearchHistory(this).readFromHistory()[0]

        initializeViews()

        addInformation()

        backButton.setOnClickListener {
            finish()
        }

        preparePlayer(track.previewUrl)

        playButton.setOnClickListener {
            playbackControl()
        }


    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainThreadHandler?.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun initializeViews() {
        cover = findViewById<ImageView>(R.id.cover)
        trackName = findViewById<TextView>(R.id.trackName)
        trackPerformer = findViewById<TextView>(R.id.performerName)
        trackDuration = findViewById<TextView>(R.id.trackDuration)
        trackYear = findViewById<TextView>(R.id.trackYear)
        trackAlbum = findViewById<TextView>(R.id.trackAlbum)
        trackGenre = findViewById<TextView>(R.id.trackGenre)
        trackCountry = findViewById<TextView>(R.id.trackCountry)
        playButton = findViewById(R.id.playButton)
        trackTime = findViewById(R.id.trackTime)
        albumGroup = findViewById<Group>(R.id.albumGroup)
    }

    private fun createUpdateTimerTask(): Runnable {
        return object : Runnable {
            override fun run() {
                trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                mainThreadHandler?.postDelayed(this, TIMER_DELAY)
            }
        }
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(previewUrl)
            prepareAsync()
            setOnPreparedListener {
                playerState = STATE_PREPARED
            }
            setOnCompletionListener {
                playButton.setImageResource(R.drawable.play)
                playerState = STATE_PREPARED
                mainThreadHandler?.removeCallbacks(updateTimerTask)
                trackTime.text = "00:00"
            }
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause)
        playerState = STATE_PLAYING
        mainThreadHandler?.post(updateTimerTask)
    }

    private fun pausePlayer() {
        mainThreadHandler?.removeCallbacks(updateTimerTask)
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play)
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
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
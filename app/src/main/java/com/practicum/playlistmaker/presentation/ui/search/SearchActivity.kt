package com.practicum.playlistmaker.presentation.ui.search

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.presentation.ui.track.TrackAdapter
import com.practicum.playlistmaker.presentation.ui.track.TrackVeiwHolder
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {
    private var editTextValue: String = NAME_DEF

//    private lateinit var rvHistoryTrack: RecyclerView
//    private lateinit var historyTracksList: ArrayList<Track>

    private lateinit var rvHistoryTrack: RecyclerView
    private lateinit var tracksListView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var historyMessage: TextView
    private lateinit var cleanHistoryButton: Button

    private lateinit var searchRunnable: Runnable

    private val tracksList = arrayListOf<Track>()
    private val trackAdapter = TrackAdapter(tracksList)


    private var historyTracksList = arrayListOf<Track>()


    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val tracksInteractor = Creator.provideTracksInteractor()
    private val searchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchRunnable = Runnable { search() }

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderErrorImage = findViewById(R.id.ErrorСover)
        progressBar = findViewById(R.id.progressBar)
        val linearLayout = findViewById<LinearLayout>(R.id.container)
        inputEditText = findViewById<EditText>(R.id.inputEditText)
        placeholderRefreshButton = findViewById<Button>(R.id.refresh)
        val clearButton = findViewById<Button>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.arrowBack)


        historyMessage = findViewById(R.id.historyMessage)
        cleanHistoryButton = findViewById(R.id.cleanHistoryButton)

        historyTracksList = searchHistoryInteractor.readFromHistory()

        val trackHistoryAdapter = TrackAdapter(historyTracksList)

        rvHistoryTrack = findViewById<RecyclerView>(R.id.rvHistoryTrack)
        rvHistoryTrack.adapter = trackHistoryAdapter

        inputEditText.setText(editTextValue)

        backButton.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            handler.removeCallbacksAndMessages(null)
            inputEditText.setText("")
            tracksList.clear()
            trackAdapter.notifyDataSetChanged()
            clearViewsWhenEmpty()
        }

        placeholderRefreshButton.setOnClickListener {
            search()
        }

        inputEditText.setOnFocusChangeListener { v, hasFocus ->
            handler.removeCallbacks(searchRunnable)
            historyViewsVisibility()
            if (hasFocus && inputEditText.text.isEmpty()) {
                tracksList.clear()
                trackAdapter.notifyDataSetChanged()
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null)
                editTextValue = s.toString()
                clearButton.visibility = clearButtonVisibility(s)

                historyViewsVisibility()

                searchDebounce()

                if (inputEditText.hasFocus() && inputEditText.text.isEmpty()) {
                    tracksList.clear()
                    trackAdapter.notifyDataSetChanged()
                }
                clearViewsWhenEmpty()

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)


        tracksListView = findViewById(R.id.rvTrack)
        tracksListView.adapter = trackAdapter


        cleanHistoryButton.setOnClickListener {
            historyTracksList.clear()
            searchHistoryInteractor.saveToHistory(historyTracksList)
            trackHistoryAdapter.notifyDataSetChanged()
            historyMessage.visibility = View.GONE
            cleanHistoryButton.visibility = View.GONE
            rvHistoryTrack.visibility = View.GONE
        }


        trackAdapter.onTrackClickListener = TrackVeiwHolder.OnTrackClickListener { item->
            if (clickDebounce()) {
                historyTracksList.removeIf {it.trackId == item.trackId}
                if(historyTracksList.size > 9) {
                    historyTracksList.removeLast()
                }
                historyTracksList.add(0,item)
                trackHistoryAdapter.notifyDataSetChanged()
                searchHistoryInteractor.saveToHistory(historyTracksList)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                startActivity(playerIntent)
            }


        }

        trackHistoryAdapter.onTrackClickListener = TrackVeiwHolder.OnTrackClickListener { item->
            if (clickDebounce()) {
                historyTracksList.removeIf {it.trackId == item.trackId}
                if(historyTracksList.size > 9) {
                    historyTracksList.removeLast()
                }
                historyTracksList.add(0,item)
                trackHistoryAdapter.notifyDataSetChanged()
                searchHistoryInteractor.saveToHistory(historyTracksList)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                startActivity(playerIntent)
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                handler.removeCallbacks(searchRunnable)
                search()
                true
            }
            false
        }
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
            handler.removeCallbacks(searchRunnable)
            handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    private fun search() {
        if (inputEditText.text.isNotEmpty()) {

            placeholderMessage.visibility = View.GONE
            placeholderErrorImage.visibility = View.GONE
            placeholderRefreshButton.visibility = View.GONE
            tracksListView.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            tracksInteractor.search(inputEditText.text.toString(),object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>, response: Int) {
                    if (response == 200) {
                        handler.post {
                            progressBar.visibility = View.GONE
                            if (inputEditText.text.isNotEmpty()) {
                                tracksList.clear()
                                if (foundTracks.isNotEmpty()) {
                                    tracksListView.visibility = View.VISIBLE
                                    tracksList.addAll(foundTracks)
                                    trackAdapter.notifyDataSetChanged()
                                }
                                if (tracksList.isEmpty()) {
                                    showMessage(getString(R.string.nothing_found), "")
                                    placeholderErrorImage.setImageResource(R.drawable.nothing_found)
                                    showErrorImage(getString(R.string.nothing_found), "")
                                    showRefreshButton("","")
                                } else {
                                    hideMessage()
                                    showErrorImage("", "")
                                    showRefreshButton("","")
                                }
                            }
                        }
                    } else {
                        handler.post {
                            progressBar.visibility = View.GONE
                            showMessage(
                                getString(R.string.something_went_wrong),
                                response.toString()
                            )
                            placeholderErrorImage.setImageResource(R.drawable.connection_problems)
                            showRefreshButton(getString(R.string.something_went_wrong), "")
                            showErrorImage(getString(R.string.something_went_wrong), "")
                        }
                    }

                }
            })
        }
    }



    private fun showMessage(text: String,additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracksList.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
            }
        } else {
            placeholderMessage.visibility = View.GONE
        }
    }
    private fun hideMessage() {
            placeholderMessage.visibility = View.GONE
    }
    private fun showErrorImage(text: String,additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderErrorImage.visibility = View.VISIBLE
            tracksList.clear()
            trackAdapter.notifyDataSetChanged()
        } else {
            placeholderErrorImage.visibility = View.GONE
        }
    }

    private fun showRefreshButton(text: String,additionalMessage: String) {

        if (text.isNotEmpty()) {
            placeholderRefreshButton.visibility = View.VISIBLE
            tracksList.clear()
            trackAdapter.notifyDataSetChanged()
        } else {
            placeholderRefreshButton.visibility = View.GONE
        }
    }
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearViewsWhenEmpty() {
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderErrorImage.visibility = View.GONE
        placeholderRefreshButton.visibility = View.GONE
    }

    private fun historyViewsVisibility() {
        if (inputEditText.hasFocus() && inputEditText.text.isEmpty() &&  historyTracksList.size != 0) {
            historyMessage.visibility = View.VISIBLE
            cleanHistoryButton.visibility = View.VISIBLE
            rvHistoryTrack.visibility = View.VISIBLE
        } else {
            historyMessage.visibility = View.GONE
            cleanHistoryButton.visibility = View.GONE
            rvHistoryTrack.visibility = View.GONE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_NAME, editTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(SEARCH_NAME, NAME_DEF)
    }

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}


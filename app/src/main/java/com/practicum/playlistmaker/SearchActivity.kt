package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
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
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"
    private var editTextValue: String = NAME_DEF
    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    private lateinit var sharedPrefs : SharedPreferences
    private lateinit var rvHistoryTrack: RecyclerView
    private lateinit var historyTracksList: ArrayList<Track>



    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)


    private val tracksList = arrayListOf<Track>()
    private val trackAdapter =TrackAdapter(tracksList)

    private lateinit var tracksListView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var progressBar: ProgressBar

    private lateinit var historyMessage: TextView
    private lateinit var cleanHistoryButton: Button

    private lateinit var searchRunnable: Runnable

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var isClickAllowed = true
    //private val searchRunnable = Runnable { search() }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPrefs = getSharedPreferences(HISTORY_TRACK_PREFERENCES, MODE_PRIVATE)

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

        historyTracksList = SearchHistory(sharedPrefs).read()
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
            SearchHistory(sharedPrefs).write(historyTracksList)
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
                SearchHistory(sharedPrefs).write(historyTracksList)
                val playerIntent = Intent(this, PlayerActivity::class.java)
                startActivity(playerIntent)
            }


        }

        trackHistoryAdapter.onTrackClickListener = TrackVeiwHolder.OnTrackClickListener {item->
            if (clickDebounce()) {
                historyTracksList.removeIf {it.trackId == item.trackId}
                if(historyTracksList.size > 9) {
                    historyTracksList.removeLast()
                }
                historyTracksList.add(0,item)
                trackHistoryAdapter.notifyDataSetChanged()
                SearchHistory(sharedPrefs).write(historyTracksList)
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


            iTunesService.search(inputEditText.text.toString()).enqueue(object :
                Callback<TracksResponse> {

                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (inputEditText.text.isEmpty()) {
                        return
                    }
                    if (response.isSuccessful) {
                        tracksList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            tracksListView.visibility = View.VISIBLE
                            tracksList.addAll(response.body()?.results!!)
                            trackAdapter.notifyDataSetChanged()
                        }
                        if (tracksList.isEmpty()) {
                            showMessage(getString(R.string.nothing_found), "")
                            if(isDarkModeOn()) {
                                placeholderErrorImage.setImageResource(R.drawable.nothing_found_dark_mode)
                            } else {
                                placeholderErrorImage.setImageResource(R.drawable.nothing_found_light_mode)
                            }
                            showErrorImage(getString(R.string.nothing_found), "")
                            showRefreshButton("","")
                        } else {
                            hideMessage()
                            showErrorImage("", "")
                            showRefreshButton("","")
                        }
                    } else {
                        showMessage(getString(R.string.something_went_wrong), response.code().toString())
                        if(isDarkModeOn()) {
                            placeholderErrorImage.setImageResource(R.drawable.connection_problems_dark_mode)
                            showRefreshButton(getString(R.string.something_went_wrong), "")
                        } else {
                            placeholderErrorImage.setImageResource(R.drawable.connection_problems_light_mode)
                            showRefreshButton(getString(R.string.something_went_wrong), "")
                        }

                        showErrorImage(getString(R.string.something_went_wrong), "")
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    if (inputEditText.text.isEmpty()) {
                        return
                    }
                    showMessage(getString(R.string.something_went_wrong), t.message.toString())
                    if(isDarkModeOn()) {
                        placeholderErrorImage.setImageResource(R.drawable.connection_problems_dark_mode)
                        showRefreshButton(getString(R.string.something_went_wrong), "")
                    } else {
                        placeholderErrorImage.setImageResource(R.drawable.connection_problems_light_mode)
                        showRefreshButton(getString(R.string.something_went_wrong), "")
                    }

                    showErrorImage(getString(R.string.something_went_wrong), "")
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
    fun isDarkModeOn(): Boolean {
        val nightModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isDarkModeOn = nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        return isDarkModeOn
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

}


package com.practicum.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
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

    private val iTunesService = retrofit.create(ITunesSearchApi::class.java)


    private val tracksList = arrayListOf<Track>()
    private val trackAdapter =TrackAdapter(tracksList)
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var inputEditText: EditText

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderErrorImage = findViewById(R.id.ErrorСover)
        val linearLayout = findViewById<LinearLayout>(R.id.container)
        inputEditText = findViewById<EditText>(R.id.inputEditText)
        placeholderRefreshButton = findViewById<Button>(R.id.refresh)
        val clearButton = findViewById<Button>(R.id.clearIcon)
        val backButton = findViewById<Button>(R.id.arrowBack)

        inputEditText.setText(editTextValue)

        backButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracksList.clear()
            trackAdapter.notifyDataSetChanged()
        }
        placeholderRefreshButton.setOnClickListener {
            search()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                editTextValue = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)

        val rvTrack = findViewById<RecyclerView>(R.id.rvTrack)

        rvTrack.adapter = trackAdapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false


        }
    }
    private fun search() {
        if (inputEditText.text.isNotEmpty()) {
            iTunesService.search(inputEditText.text.toString()).enqueue(object :
                Callback<TracksResponse> {
                override fun onResponse(call: Call<TracksResponse>,
                                        response: Response<TracksResponse>
                ) {
                    if (response.code() == 200) {
                        tracksList.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
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
                            showMessage("", "")
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_NAME, editTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(SEARCH_NAME, NAME_DEF)
    }

}
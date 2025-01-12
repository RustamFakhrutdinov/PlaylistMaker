package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.mediateka.ui.MediatekaFragmentDirections
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.state.SearchState
import com.practicum.playlistmaker.search.ui.track.TrackAdapter
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment: Fragment() {

    private var editTextValue: String = NAME_DEF

    private lateinit var rvHistoryTrack: RecyclerView
    private lateinit var tracksListView: RecyclerView
    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderErrorImage: ImageView
    private lateinit var placeholderRefreshButton: Button
    private lateinit var inputEditText: EditText
    private lateinit var searchTextWatcher: TextWatcher
    private lateinit var progressBar: ProgressBar

    private lateinit var historyMessage: TextView
    private lateinit var cleanHistoryButton: Button

    private val tracksList = arrayListOf<Track>()
    private val trackAdapter = TrackAdapter(tracksList)


    private var historyTracksList = arrayListOf<Track>()
    private val historyTrackAdapter = TrackAdapter(historyTracksList)

    private val viewModel: SearchViewModel by viewModel()
    private lateinit var binding: FragmentSearchBinding

    private lateinit var clickDebounce: (Track) -> Unit
    private lateinit var clickDebounceHistory: (Track) -> Unit
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        placeholderMessage = binding.placeholderMessage
        placeholderErrorImage = binding.errorCover
        inputEditText = binding.inputEditText
        placeholderRefreshButton = binding.refresh
        progressBar = binding.progressBar
        val clearButton = binding.clearIcon

        historyMessage = binding.historyMessage
        cleanHistoryButton = binding.cleanHistoryButton

        rvHistoryTrack = binding.rvHistoryTrack
        rvHistoryTrack.adapter = historyTrackAdapter

        tracksListView = binding.rvTrack
        tracksListView.adapter = trackAdapter

        inputEditText.setText(editTextValue)

        searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = clearButtonVisibility(s)
                viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                if (inputEditText.hasFocus() && inputEditText.text.isEmpty()) {
                    viewModel.showSearchHistory()
                } else {
                    hideHistory()
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        searchTextWatcher?.let { inputEditText.addTextChangedListener(it) }


        cleanHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        clickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { item ->
            viewModel.saveToSearchHistory(item)
            val direction: NavDirections = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(item)
            findNavController().navigate(direction)
        }

        clickDebounceHistory = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { item ->
            viewModel.saveToSearchHistory(item)
            viewModel.showSearchHistory()
            val direction: NavDirections = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(item)
            findNavController().navigate(direction)
        }

        trackAdapter.onTrackClickListener = TrackViewHolder.OnTrackClickListener { item->
            clickDebounce(item)
        }

        historyTrackAdapter.onTrackClickListener = TrackViewHolder.OnTrackClickListener { item->
            clickDebounceHistory(item)
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.searchOnButton(inputEditText.text.toString())
                true
            }
            false
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
        }

        placeholderRefreshButton.setOnClickListener {
            viewModel.searchOnButton(inputEditText.text.toString())
        }

        inputEditText.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.showSearchHistory()
            }
        }

        viewModel.observeHistory().observe(viewLifecycleOwner) {
            showSearchHistory(it)
        }
        viewModel.observeState().observe(viewLifecycleOwner) {
            hideHistory()
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchTextWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_NAME, editTextValue)
    }

    private fun showToast(additionalMessage: String) {
        Toast.makeText(requireContext(), additionalMessage, Toast.LENGTH_LONG).show()
    }

    private fun render(state: SearchState) {
        when (state) {
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.Empty -> showEmpty(state.message)
            is SearchState.Error -> showError(state.errorMessage)
            is SearchState.Loading -> showLoading()
        }
    }

    private fun showLoading() {
        tracksListView.isVisible = false
        rvHistoryTrack.isVisible = false
        placeholderMessage.isVisible = false
        progressBar.isVisible = true
        cleanHistoryButton.isVisible = false
        historyMessage.isVisible = false
        placeholderErrorImage.isVisible = false
    }

    private fun showError(errorMessage: String) {
        placeholderErrorImage.setImageResource(R.drawable.connection_problems)
        tracksListView.isVisible = false
        rvHistoryTrack.isVisible = false
        historyMessage.isVisible = false
        cleanHistoryButton.isVisible = false
        placeholderMessage.isVisible = true
        progressBar.isVisible = false
        placeholderErrorImage.isVisible = true

        placeholderMessage.text = errorMessage
    }

    private fun showEmpty(emptyMessage: String) {
        showError(emptyMessage)
        placeholderErrorImage.setImageResource(R.drawable.nothing_found)
    }

    private fun showContent(tracks: List<Track>) {
        tracksListView.isVisible = true
        placeholderMessage.isVisible = false
        binding.progressBar.isVisible = false

        tracksList.clear()
        tracksList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showSearchHistory(history: List<Track>) {
        if (history.isNotEmpty()) {
            rvHistoryTrack.isVisible = true
            tracksListView.isVisible = false
            progressBar.isVisible = false
            cleanHistoryButton.isVisible = true
            historyMessage.isVisible = true
            placeholderMessage.isVisible = false
            placeholderErrorImage.isVisible = false
            historyTracksList.clear()
            historyTracksList.addAll(history)
            historyTrackAdapter.notifyDataSetChanged()

        } else {
            rvHistoryTrack.isVisible = false
            tracksListView.isVisible = false
            placeholderMessage.isVisible = false
            placeholderErrorImage.isVisible = false
            cleanHistoryButton.isVisible = false
            historyMessage.isVisible = false
        }
    }

    private fun hideHistory() {
        rvHistoryTrack.isVisible = false
        tracksListView.isVisible = false
        placeholderMessage.isVisible = false
        placeholderErrorImage.isVisible = false
        cleanHistoryButton.isVisible = false
        historyMessage.isVisible = false
    }

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L


        const val TAG = "SearchFragment"

        fun createArgs(): Bundle =
            bundleOf()
    }





}
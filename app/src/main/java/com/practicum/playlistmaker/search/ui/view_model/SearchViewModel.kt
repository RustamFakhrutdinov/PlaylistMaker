package com.practicum.playlistmaker.search.ui.view_model

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.history.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.state.SearchState
import com.practicum.playlistmaker.util.SingleLiveEvent

class SearchViewModel(private val tracksInteractor: TracksInteractor,
                      private val searchHistoryInteractor: SearchHistoryInteractor,
                      application: Application): AndroidViewModel(application) {

    private val handler = Handler(Looper.getMainLooper())

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData


    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast


    private val searchHistoryLiveData = MutableLiveData<List<Track>>()
    fun observeHistory(): LiveData<List<Track>> = searchHistoryLiveData

    private var latestSearchText: String? = null

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun showSearchHistory() {
        searchHistoryLiveData.postValue(searchHistoryInteractor.readFromHistory())
    }

    fun clearSearchHistory() {
        searchHistoryInteractor.clearSearchHistory()
        searchHistoryLiveData.postValue(searchHistoryInteractor.readFromHistory())
    }
    fun saveToSearchHistory(item:Track) {
        searchHistoryInteractor.saveToHistory(item)
    }
    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }

    fun searchOnButton(editText: String) {
        this.latestSearchText = editText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        searchRequest(editText)
    }

     private fun searchRequest(inputEditText: String) {
        if (inputEditText.isNotEmpty()) {
            renderState(SearchState.Loading)

            tracksInteractor.search(inputEditText,object : TracksInteractor.TracksConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        val tracks = mutableListOf<Track>()
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    SearchState.Error(
                                        errorMessage = getApplication<Application>().getString(R.string.something_went_wrong),
                                    )
                                )
                                showToast.postValue(errorMessage)
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    SearchState.Empty(
                                        message = getApplication<Application>().getString(R.string.nothing_found),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    SearchState.Content(
                                        tracks = tracks,
                                    )
                                )
                            }
                        }
                }
            })
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }


}
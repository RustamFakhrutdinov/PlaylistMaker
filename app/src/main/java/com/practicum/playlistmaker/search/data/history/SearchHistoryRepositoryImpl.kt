package com.practicum.playlistmaker.search.data.history

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

const val HISTORY_TRACK_KEY = "key_for_history_track"
const val HISTORY_TRACK_PREFERENCES = "history_track_preferences"


class SearchHistoryRepositoryImpl(private val sharedPrefs: SharedPreferences,
                                  private val json: Gson): SearchHistoryRepository {
   // private val sharedPrefs = context.getSharedPreferences(HISTORY_TRACK_PREFERENCES, Application.MODE_PRIVATE)


    override fun readFromHistory(): ArrayList<Track> {

        val json: String? = sharedPrefs.getString(HISTORY_TRACK_KEY, null) ?: return arrayListOf()
        val itemType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, itemType)


    }

    override fun clearSearchHistory() {
        sharedPrefs.edit().remove(HISTORY_TRACK_KEY).apply()
    }

    // запись
    override fun saveToHistory(track: Track) {
        val arrayList = readFromHistory()
        arrayList.removeIf {it.trackId == track.trackId}
        if(arrayList.size > 9) {
            arrayList.removeLast()
        }
        arrayList.add(0,track)
        //val json = Gson().toJson(arrayList)
        val json = json.toJson(arrayList)
        sharedPrefs.edit()
            .putString(HISTORY_TRACK_KEY, json)
            .apply()
    }



}
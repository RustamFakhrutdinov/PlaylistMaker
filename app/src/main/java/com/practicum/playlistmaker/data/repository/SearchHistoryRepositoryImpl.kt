package com.practicum.playlistmaker.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track

const val HISTORY_TRACK_PREFERENCES = "history_track_preferences"
const val HISTORY_TRACK_KEY = "key_for_history_track"




class SearchHistoryRepositoryImpl(private val context: Context): SearchHistoryRepository {
    private val sharedPreferences = context.getSharedPreferences(HISTORY_TRACK_PREFERENCES, Context.MODE_PRIVATE)



    override fun readFromHistory(): ArrayList<Track> {

        val json: String? = sharedPreferences.getString(HISTORY_TRACK_KEY, null) ?: return arrayListOf()
        val itemType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, itemType)


    }

    // запись
    override fun saveToHistory(tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_TRACK_KEY, json)
            .apply()
    }



}
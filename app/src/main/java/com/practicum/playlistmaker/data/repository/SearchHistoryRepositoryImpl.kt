package com.practicum.playlistmaker.data.repository

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.history.SearchHistoryRepository
import com.practicum.playlistmaker.domain.models.Track



const val HISTORY_TRACK_KEY = "key_for_history_track"
const val HISTORY_TRACK_PREFERENCES = "history_track_preferences"


class SearchHistoryRepositoryImpl(context: Context): SearchHistoryRepository {
    private val sharedPrefs = context.getSharedPreferences(HISTORY_TRACK_PREFERENCES, Application.MODE_PRIVATE)


    override fun readFromHistory(): ArrayList<Track> {

        val json: String? = sharedPrefs.getString(HISTORY_TRACK_KEY, null) ?: return arrayListOf()
        val itemType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, itemType)


    }

    // запись
    override fun saveToHistory(tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPrefs.edit()
            .putString(HISTORY_TRACK_KEY, json)
            .apply()
    }



}
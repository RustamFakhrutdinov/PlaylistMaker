package com.practicum.playlistmaker

import android.content.SharedPreferences
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val HISTORY_TRACK_PREFERENCES = "history_track_preferences"
const val HISTORY_TRACK_KEY = "key_for_history_track"




class SearchHistory(private val sharedPreferences: SharedPreferences) {



    fun read(): ArrayList<Track> {

        val json: String? = sharedPreferences.getString(HISTORY_TRACK_KEY, null) ?: return arrayListOf()
        val itemType = object : TypeToken<List<Track>>() {}.type
        return Gson().fromJson(json, itemType)


    }

    // запись
    fun write(tracks: ArrayList<Track>) {
        val json = Gson().toJson(tracks)
        sharedPreferences.edit()
            .putString(HISTORY_TRACK_KEY, json)
            .apply()
    }



}
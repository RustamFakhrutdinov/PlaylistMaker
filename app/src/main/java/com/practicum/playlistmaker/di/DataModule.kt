package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.mediateka.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.history.HISTORY_TRACK_PREFERENCES
import com.practicum.playlistmaker.search.data.network.ITunesSearchApi
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.settings.data.DARK_THEME_PREFERENCES
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesSearchApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesSearchApi::class.java)
    }

    single(named("historyPrefs")) {
        androidContext()
            .getSharedPreferences(HISTORY_TRACK_PREFERENCES, Context.MODE_PRIVATE)
    }

    single(named("themePrefs")) {
        androidContext()
            .getSharedPreferences(DARK_THEME_PREFERENCES, Context.MODE_PRIVATE)
    }


    factory { Gson() }

    single<NetworkClient> {
        RetrofitNetworkClient(get(), androidContext())
    }

    single {
        ExternalNavigator(androidContext())
    }

    factory {
        MediaPlayer()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single {
        get<AppDatabase>().trackFavouriteDao()
    }

}
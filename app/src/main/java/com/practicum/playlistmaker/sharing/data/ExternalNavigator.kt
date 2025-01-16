package com.practicum.playlistmaker.sharing.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.util.App

class ExternalNavigator(private val context: Context) {
    fun shareLink(shareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = context.getString(R.string.type_for_all_messengers)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(
            Intent.createChooser(
                shareIntent,
                context.getString(R.string.title_share_app)
            ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openLink(termsLink: String) {
        val webpage: Uri = Uri.parse(termsLink)
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, webpage)
        userAgreementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(userAgreementIntent)
    }

    fun openEmail(supportEmailData: EmailData) {
        val subject = supportEmailData.subject
        val message = supportEmailData.body
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        supportIntent.data = Uri.parse(context.getString(R.string.mailto))
        supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmailData.email))
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, message)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(supportIntent)
    }

    fun sharePlaylist(playlist: Playlist, trackList: List<Track>) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = context.getString(R.string.type_for_all_messengers)
        val trackCount = playlist.count
        val description: String =
            if (!playlist.description.isNullOrEmpty()) playlist.description + "\n" else ""
        var shareText = playlist.name + "\n" +
                description +
                trackCount + " трек" + getTrackWordEnding(trackCount)
        var number = 1
        for (track in trackList) {
            shareText += ("\n" + number.toString() + ". " + track.artistName + " - " +
                    track.trackName + " (" + track.trackTime + ")")
            number++
        }
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(
                Intent.createChooser(
                    shareIntent,
                    context.getString(R.string.share_playlist)
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                context, context.resources.getString(R.string.share_playlist_error),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun getTrackWordEnding(count: Int): String {
        return when {
            count % 100 in 11..19 -> "ов" // Числа от 11 до 19
            count % 10 == 1 -> ""         // Оканчивается на 1, кроме 11
            count % 10 in 2..4 -> "а"    // Оканчивается на 2, 3, 4, кроме 12-14
            else -> "ов"                 // Все остальные случаи
        }
    }
}
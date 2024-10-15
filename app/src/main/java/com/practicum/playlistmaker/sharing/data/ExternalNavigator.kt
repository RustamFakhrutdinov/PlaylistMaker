package com.practicum.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.util.App

class ExternalNavigator(private val context: Context) {
    fun shareLink(shareAppLink: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = context.getString(R.string.type_for_all_messengers)
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareAppLink)
        context.startActivity(Intent.createChooser(
            shareIntent,
            context.getString(R.string.title_share_app))
        )
    }
    fun openLink(termsLink: String) {
        val webpage: Uri = Uri.parse(termsLink)
        val userAgreementIntent = Intent(Intent.ACTION_VIEW, webpage)
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
        context.startActivity(supportIntent)
    }
}
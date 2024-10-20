package com.practicum.playlistmaker.sharing.data.impl

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.data.ExternalNavigator
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.EmailData
import com.practicum.playlistmaker.util.App

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    ):SharingInteractor {
    private val application = App.getAppContext()
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        return application.getString(R.string.message_to_share_with_messegers)
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData(email = application.getString(R.string.my_email),
            body = application.getString(R.string.message_to_share_with_email),
            subject = application.getString(R.string.subject_for_email))
    }

    private fun getTermsLink(): String {
        return application.getString(R.string.url_for_user_agreement)
    }
}

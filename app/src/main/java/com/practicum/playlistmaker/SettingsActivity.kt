package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareButton = findViewById<Button>(R.id.share)
        val supportButton = findViewById<Button>(R.id.support)
        val userAgreementButton = findViewById<Button>(R.id.user_agreement)
        val backButton = findViewById<Button>(R.id.arrowBack)

        backButton.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        }


        shareButton.setOnClickListener {
            val message = resources.getString(R.string.message_to_share_with_messegers)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = resources.getString(R.string.type_for_all_messengers)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }

        supportButton.setOnClickListener {
            val subject = resources.getString(R.string.subject_for_email)
            val message = resources.getString(R.string.message_to_share_with_email)
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            supportIntent.data = Uri.parse(resources.getString(R.string.mailto))
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(resources.getString(R.string.my_email)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(supportIntent)
        }

        userAgreementButton.setOnClickListener {
            val url = resources.getString(R.string.url_for_user_agreement)
            val webpage: Uri = Uri.parse(url)
            val userAgreementIntent = Intent(Intent.ACTION_VIEW, webpage)
            startActivity(userAgreementIntent)

        }
    }


}
package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

const val DARK_THEME_PREFERENCES = "dark_theme_preferences"
const val DARK_THEME_KEY = "key_for_dark_theme"
class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val shareButton = findViewById<Button>(R.id.share)
        val supportButton = findViewById<Button>(R.id.support)
        val userAgreementButton = findViewById<Button>(R.id.user_agreement)
        val backButton = findViewById<Button>(R.id.arrowBack)

        backButton.setOnClickListener {
//            val mainIntent = Intent(this, MainActivity::class.java)
//            startActivity(mainIntent)
            finish()
        }

        val sharedPrefs = getSharedPreferences(DARK_THEME_PREFERENCES, MODE_PRIVATE)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        themeSwitcher.isChecked = sharedPrefs.getBoolean(DARK_THEME_KEY, false)

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            sharedPrefs.edit().putBoolean(DARK_THEME_KEY, checked).apply()
            (applicationContext as App).switchTheme(checked)
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
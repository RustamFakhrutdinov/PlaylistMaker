package com.practicum.playlistmaker.mediateka.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding

class MediatekaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatekaBinding

    private lateinit var tabMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton = binding.arrowBack

        backButton.setOnClickListener {
            finish()
        }

//        val trackPosition = intent.getStringExtra("poster") ?: ""
//        val playlistNumber = intent.getIntExtra("id") ?: ""

        binding.viewPager.adapter = MediatekaViewPagerAdapter(
            fragmentManager = supportFragmentManager,
            lifecycle = lifecycle,
//            trackPosition = trackPosition,
//            playlistNumber = playlistNumber,
        )

        tabMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = getString(R.string.favorites_tracks)
                1 -> tab.text = getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }

}
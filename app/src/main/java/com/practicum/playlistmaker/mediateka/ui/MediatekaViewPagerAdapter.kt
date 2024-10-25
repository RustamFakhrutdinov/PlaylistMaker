package com.practicum.playlistmaker.mediateka.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediatekaViewPagerAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
//    private val trackPosition: Int,
//    private val playlistNumber: Int,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> FavoritesFragment.newInstance(1)
            else -> PlaylistFragment.newInstance(2)
        }
    }
}
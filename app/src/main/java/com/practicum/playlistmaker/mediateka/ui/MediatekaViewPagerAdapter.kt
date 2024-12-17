package com.practicum.playlistmaker.mediateka.ui

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class MediatekaViewPagerAdapter (
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        Log.d("MediatekaViewPagerAdapter", "Creating fragment for position $position")
        return when(position) {
            0 -> FavoritesFragment.newInstance(1)
            else -> PlaylistFragment.newInstance(2)
        }
    }
}
package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlayListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PlaylistFragment: Fragment() {
    companion object {
        private const val PLAYLIST_NUM = "playlist_number"

        fun newInstance(playlistNumber: Int) = PlaylistFragment().apply {
            arguments = Bundle().apply {
                putInt(PLAYLIST_NUM, playlistNumber)
            }
        }
    }

    private val playlistViewModel: PlayListViewModel by viewModel {
        parametersOf(requireArguments().getInt(PLAYLIST_NUM))
    }

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistViewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
            showEmpty(it)
        }
    }

    private fun showEmpty(position: Int) {
        binding.apply {
            createPlaylistButton.isVisible = true
            coverEmpty.isVisible = true
            placeholderMessage.isVisible = true
            placeholderMessage.text = "Вы не создали \nни одного плейлиста"
        }
    }
}

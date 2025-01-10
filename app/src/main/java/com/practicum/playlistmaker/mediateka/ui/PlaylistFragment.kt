package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.state.FavouriteTracksState
import com.practicum.playlistmaker.mediateka.ui.state.PlaylistState
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistViewModel
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

    private val playlistViewModel: PlaylistViewModel by viewModel()
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

       // playlistViewModel.fillData()

//        playlistViewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
//            render(it)
//        }
        showEmpty("afdsghfgh")
        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediatekaFragment_to_newPlaylistFragment)
        }
    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty(state.message)
            is PlaylistState.Content ->showContent(state.playlists)
        }
    }

    private fun showContent(playlists: List<Playlist>) {

    }

    private fun showEmpty(message: String) {
        binding.apply {
            createPlaylistButton.isVisible = true
            coverEmpty.isVisible = true
            placeholderMessage.isVisible = true
            placeholderMessage.text = message
        }
    }
}

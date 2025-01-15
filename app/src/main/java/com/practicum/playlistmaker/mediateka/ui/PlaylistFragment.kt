package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.playlist.PlaylistAdapter
import com.practicum.playlistmaker.mediateka.ui.playlist.PlaylistViewHolder
import com.practicum.playlistmaker.mediateka.ui.state.FavouriteTracksState
import com.practicum.playlistmaker.mediateka.ui.state.PlaylistState
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragmentDirections
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import com.practicum.playlistmaker.util.debounce
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
    private lateinit var playlistListView: RecyclerView
    private var playlistList = mutableListOf<Playlist>()
    private val playlistAdapter = PlaylistAdapter(playlistList)

    private val playlistViewModel: PlaylistViewModel by viewModel()
    private lateinit var binding: FragmentPlaylistBinding

    private lateinit var clickDebounce: (Playlist) -> Unit

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

        playlistViewModel.fillData()

        playlistViewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
            render(it)
        }
        binding.createPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_mediatekaFragment_to_newPlaylistFragment)
        }
        clickDebounce = debounce<Playlist>(500L, viewLifecycleOwner.lifecycleScope, false) { item ->
            val direction: NavDirections = MediatekaFragmentDirections.actionMediatekaFragmentToChosenPlaylistFragment(item)
            findNavController().navigate(direction)
        }
        playlistAdapter.onPlaylistClickListener= PlaylistViewHolder.OnPlaylistClickListener {item ->
            clickDebounce(item)

        }


    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty(state.message)
            is PlaylistState.Content ->showContent(state.playlists)
        }
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            createPlaylistButton.isVisible = true
            coverEmpty.isVisible = false
            placeholderMessage.isVisible = false
            rvPlaylistItem.isVisible = true
            playlistListView = binding.rvPlaylistItem
            playlistListView.layoutManager = GridLayoutManager(requireContext(), 2)
            playlistListView.adapter = playlistAdapter
        }
        playlistList.clear()
        playlistList.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()

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

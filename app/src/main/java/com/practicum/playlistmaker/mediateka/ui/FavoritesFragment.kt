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
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.mediateka.ui.state.FavouriteTracksState
import com.practicum.playlistmaker.mediateka.ui.viewmodel.FavoritesViewModel
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.state.SearchState
import com.practicum.playlistmaker.search.ui.track.TrackAdapter
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FavoritesFragment : Fragment() {

    companion object {
        private const val POSITION = "position"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(position: Int) = FavoritesFragment().apply {
            arguments = Bundle().apply {
                putInt(POSITION, position)
            }
        }
    }
    private lateinit var tracksListView: RecyclerView

    private val favoritesViewModel: FavoritesViewModel by viewModel()
    private val tracksList = arrayListOf<Track>()
    private val trackAdapter = TrackAdapter(tracksList)

    private lateinit var clickDebounce: (Track) -> Unit

    private lateinit var binding: FragmentFavoritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesViewModel.fillData()
        favoritesViewModel.getFavoritesLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        clickDebounce = debounce<Track>(FavoritesFragment.CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { item ->
            val direction: NavDirections = MediatekaFragmentDirections.actionMediatekaFragmentToPlayerActivity(item)
            findNavController().navigate(direction)

        }

        trackAdapter.onTrackClickListener = TrackViewHolder.OnTrackClickListener { item ->
            clickDebounce(item)
        }



    }

    private fun render(state: FavouriteTracksState) {
        when (state) {
            is FavouriteTracksState.Empty -> showEmpty(state.message)
            is FavouriteTracksState.Content ->showContent(state.tracks)
        }
    }

    private fun showEmpty(message: String) {
        binding.apply {
            cover.isVisible = true
            placeholderMessage.isVisible = true
            placeholderMessage.text = message
        }
        tracksList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun showContent(tracks: List<Track>) {
        binding.apply {
            cover.isVisible = false
            placeholderMessage.isVisible = false
            tracksListView = binding.rvFavoriteTrack
            tracksListView.adapter = trackAdapter
        }
        tracksList.clear()
        tracksList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }


}
package com.practicum.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.FavoritesFragment
import com.practicum.playlistmaker.mediateka.ui.MediatekaFragmentDirections
import com.practicum.playlistmaker.mediateka.ui.playlist.PlaylistAdapter
import com.practicum.playlistmaker.mediateka.ui.playlist.PlaylistViewHolder
import com.practicum.playlistmaker.mediateka.ui.state.PlaylistState
import com.practicum.playlistmaker.mediateka.ui.state.TrackInPlaylistState
import com.practicum.playlistmaker.player.ui.playlist_bottom_sheet.BottomSheeetPlaylistAdapter
import com.practicum.playlistmaker.player.ui.playlist_bottom_sheet.BottomSheetPlaylistViewHolder
import com.practicum.playlistmaker.player.ui.state.PlayStatus
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : Fragment() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var trackPerformer: TextView
    private lateinit var trackDuration: TextView
    private lateinit var trackYear: TextView
    private lateinit var trackAlbum: TextView
    private lateinit var trackGenre: TextView
    private lateinit var trackCountry: TextView
    private lateinit var trackTime: TextView
    private lateinit var playButton: ImageButton
    private lateinit var favouriteButton: ImageButton

    private lateinit var albumGroup: Group

    private lateinit var track: Track

    private val viewModel: PlayerViewModel by viewModel()
    private val args: PlayerFragmentArgs by navArgs()

    private lateinit var playlistListView: RecyclerView
    private var playlistList = mutableListOf<Playlist>()
    private val playlistAdapter = BottomSheeetPlaylistAdapter(playlistList)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        track = args.track
        viewModel.preparePlayer(track.previewUrl)
        viewModel.isFavourite(track)

        viewModel.fillPlaylistData()

        val bottomSheetContainer = binding.standardBottomSheet

        val overlay = binding.overlay

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistButton.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                overlay.alpha = (slideOffset + 1) / 2
            }
        })
        initializeViews()
        addInformation()

        binding.createPlaylistButton.setOnClickListener {
            val playlist = Playlist(-1, "", null, null, null, 0)
            val direction: NavDirections =
                PlayerFragmentDirections.actionPlayerFragmentToNewPlaylistFragment(playlist)
            findNavController().navigate(direction)
        }
        viewModel.getPlaylistLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.getPlayStatusLiveData().observe(viewLifecycleOwner) { playStatus ->
            playButtonChange(playStatus)
            trackTime.text = playStatus.progress

            if (playStatus.isFavourite) {
                favouriteButton.setImageResource(R.drawable.favourites_active)
            } else {
                favouriteButton.setImageResource(R.drawable.favorites)
            }
        }

        viewModel.getTrackInPlaylistLiveData().observe(viewLifecycleOwner) { state ->
            renderTrackInPlaylists(state)
        }

        playButton.setOnClickListener {
            val currentPlayStatus = viewModel.getPlayStatusLiveData().value
            if (currentPlayStatus?.isPlaying == true) {
                viewModel.pause()
            } else {
                viewModel.play()
            }
        }

        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        favouriteButton.setOnClickListener {
            viewModel.onFavoriteClicked(track)
        }

        playlistAdapter.onPlaylistClickListener =
            BottomSheetPlaylistViewHolder.OnPlaylistClickListener { item ->
                viewModel.onPlaylistClicked(track, item)
            }

    }

    private fun render(state: PlaylistState) {
        when (state) {
            is PlaylistState.Empty -> showEmpty(state.message)
            is PlaylistState.Content -> showContent(state.playlists)
        }
    }

    private fun renderTrackInPlaylists(state: TrackInPlaylistState) {
        when (state) {
            is TrackInPlaylistState.Added -> showPlaylistAdded(state.playlistName)
            is TrackInPlaylistState.NotAdded -> showPlaylistNotAdded(state.playlistName)
        }
    }

    private fun showPlaylistAdded(name: String) {
        val bottomSheetContainer = binding.standardBottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)
        // bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Toast.makeText(requireContext(), "Добавлено в плейлист $name", Toast.LENGTH_LONG).show()
    }

    private fun showPlaylistNotAdded(name: String) {
        Toast.makeText(context, "Трек уже добавлен в плейлист $name", Toast.LENGTH_LONG).show()
    }


    private fun showContent(playlists: List<Playlist>) {
        binding.apply {
            createPlaylistButton.isVisible = true
            playlistListView = binding.rvPlaylistBSItem
            playlistListView.adapter = playlistAdapter
        }
        playlistList.clear()
        playlistList.addAll(playlists)
        playlistAdapter.notifyDataSetChanged()

    }

    private fun showEmpty(message: String) {
        binding.apply {
            createPlaylistButton.isVisible = true
        }
    }

    private fun playButtonChange(playStatus: PlayStatus) {
        if (playStatus.isPlaying) {
            playButton.setImageResource(R.drawable.pause)
        } else {
            playButton.setImageResource(R.drawable.play)
        }
    }

    private fun initializeViews() {
        cover = binding.cover
        trackName = binding.trackName
        trackPerformer = binding.performerName
        trackDuration = binding.trackDuration
        trackYear = binding.trackYear
        trackAlbum = binding.trackAlbum
        trackGenre = binding.trackGenre
        trackCountry = binding.trackCountry
        playButton = binding.playButton
        trackTime = binding.trackTime
        albumGroup = binding.albumGroup
        favouriteButton = binding.favoritesButton
    }

    private fun addInformation() {
        trackName.text = track.trackName
        trackPerformer.text = track.artistName
        trackDuration.text = track.trackTime
        if (track.collectionName != null) {
            trackAlbum.text = track.collectionName
        } else {
            albumGroup.visibility = View.GONE
        }

        trackYear.text = track.releaseDate.substring(0, 4)
        trackGenre.text = track.primaryGenreName
        trackCountry.text = track.country
        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, requireContext())))
            .into(cover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
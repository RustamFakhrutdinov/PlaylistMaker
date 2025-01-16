package com.practicum.playlistmaker.mediateka.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isEmpty
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentChosenPlaylistBinding
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.state.FavouriteTracksState
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.PlayerFragmentArgs
import com.practicum.playlistmaker.player.ui.PlayerFragmentDirections
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.SearchFragmentDirections
import com.practicum.playlistmaker.search.ui.track.TrackAdapter
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChosenPlaylistFragment : Fragment() {
    private val dateFormatForMinutes by lazy { SimpleDateFormat("mm", Locale.getDefault()) }

    private lateinit var cover: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var minutesCount: TextView
    private lateinit var tracksCount: TextView
    private lateinit var confirmationDialog: MaterialAlertDialogBuilder
    private lateinit var startDrawable: Drawable
    private lateinit var shareButton: ImageView
    private lateinit var menuButton: ImageView

    //al overlay = binding.overlay

    private lateinit var playlist: Playlist
    var peekHightOfStandartBS = 0
    private val args: ChosenPlaylistFragmentArgs by navArgs()

    private lateinit var tracksListView: RecyclerView

    private val tracksList = arrayListOf<Track>()
    private val trackAdapter = TrackAdapter(tracksList)

    private lateinit var binding: FragmentChosenPlaylistBinding

    private val viewmodel by viewModel<PlaylistViewModel>()

    private lateinit var clickDebounce: (Track) -> Unit
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentChosenPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = binding.backButtonPlayer
        initializeViews()
        playlist = args.playlist

        viewmodel.loadTrackList(playlist.playlistId)
        viewmodel.getTracksLiveData().observe(viewLifecycleOwner) {
            render(it)
        }



        viewmodel.fillOnePlaylistData(playlist.playlistId)
        viewmodel.getOnePlaylistLiveData().observe(viewLifecycleOwner) {
            addInformation(it)
            playlist = it
        }

        initializeViews()
        BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            binding.menuLayout.post {
                val height = resources.displayMetrics.heightPixels - binding.menuLayout.bottom - 20
                if (peekHeight > height) {
                    peekHeight = height
                    peekHightOfStandartBS = peekHeight
                }
            }
        }

        val menuBottomSheetBehavior = BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN

        }

        binding.menuButton.setOnClickListener {
            menuBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            binding.standardBottomSheet.visibility = View.GONE
        }

        menuBottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.standardBottomSheet.visibility = View.VISIBLE
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = (slideOffset + 1) / 2
            }
        })



        //кнопка назад
        backButton.setOnClickListener {
            onBack()
        }

        //системная кнопка назад
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    onBack()
                }
            })

        clickDebounce = debounce<Track>(1000L, viewLifecycleOwner.lifecycleScope, false) { item ->
            val direction: NavDirections =
                ChosenPlaylistFragmentDirections.actionChosenPlaylistFragmentToPlayerFragment(item)
            findNavController().navigate(direction)

        }

        trackAdapter.onTrackClickListener = TrackViewHolder.OnTrackClickListener { item ->
            clickDebounce(item)
        }

        trackAdapter.onTrackLongClickListener = TrackViewHolder.OnTrackLongClickListener { track ->
            deleteTrack(track, playlist.playlistId)
        }

        shareButton.setOnClickListener {
            sharePlaylist()
        }

        binding.shareTextView.setOnClickListener {
            sharePlaylist()
        }

        binding.deletePlaylistTextView.setOnClickListener {
            BottomSheetBehavior.from(binding.menuBottomSheet).apply {
                state = BottomSheetBehavior.STATE_HIDDEN
            }
            deletePlaylist(playlist.playlistId)
        }

        binding.editPlaylistTextView.setOnClickListener {
            val direction: NavDirections = ChosenPlaylistFragmentDirections
                .actionChosenPlaylistFragmentToNewPlaylistFragment(playlist)
            findNavController().navigate(direction)
        }


    }


    private fun initializeViews() {
        cover = binding.cover
        playlistName = binding.playlistName
        playlistDescription = binding.description
        minutesCount = binding.minutesCount
        tracksCount = binding.tracksCount
        startDrawable = binding.cover.drawable
        shareButton = binding.shareButton
        menuButton = binding.menuButton

    }

    private fun onBack() {
        findNavController().navigateUp()
    }

    private fun render(state: FavouriteTracksState) {
        when (state) {
            is FavouriteTracksState.Empty -> showEmpty(state.message)
            is FavouriteTracksState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        minutesCount.text = "0 минут"
        tracksList.clear()
        trackAdapter.notifyDataSetChanged()
    }

    private fun addInformation(playlist: Playlist) {
        Glide.with(this)
            .load(playlist!!.path)
            .placeholder(R.drawable.placeholder)
            .into(cover)

        playlistName.text = playlist.name
        if (playlist.description.isNullOrBlank()) {
            playlistDescription.visibility = View.GONE
        } else {
            playlistDescription.visibility = View.VISIBLE
            playlistDescription.text = playlist.description
        }

        val count = playlist.count
        tracksCount.text = count.toString() + " " + getString(R.string.track) + getTrackWordEnding(count)

        Glide.with(this)
            .load(playlist.path)
            .placeholder(R.drawable.placeholder)
            .into(binding.coverBS)

        binding.nameBS.text = playlistName.text
        binding.countBS.text = tracksCount.text
    }

    private fun showContent(tracks: List<Track>) {
        val minutes = sumMinutes(tracks)
        val s = dateFormatForMinutes.format(minutes)

        minutesCount.text = dateFormatForMinutes.format(minutes).removePrefix("0") + " " + getMinuteSuffix(minutes)

        tracksListView = binding.rvTrackBSItem
        tracksListView.adapter = trackAdapter

        tracksList.clear()
        tracksList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun sumMinutes(tracks: List<Track>): Long {
        var sum: Long = 0
        tracks.forEach { track ->
            sum += parseTimeToMillis(track.trackTime)
        }
        return sum
    }

    private fun getMinuteSuffix(milliseconds: Long): String {
        val minutes = (milliseconds / 1000) / 60
        val lastTwoDigits = minutes % 100
        val lastDigit = minutes % 10

        return when {
            lastTwoDigits in 11L..19L -> "минут"
            lastDigit == 1L -> "минута"
            lastDigit in 2L..4L -> "минуты"
            else -> "минут"
        }
    }

    private fun getTrackWordEnding(count: Int): String {
        return when {
            count % 100 in 11..19 -> "ов" // Числа от 11 до 19
            count % 10 == 1 -> ""         // Оканчивается на 1, кроме 11
            count % 10 in 2..4 -> "а"    // Оканчивается на 2, 3, 4, кроме 12-14
            else -> "ов"                 // Все остальные случаи
        }
    }

    private fun parseTimeToMillis(time: String): Long {
        val parts = time.split(":")
        val minutes = parts[0].toInt()
        val seconds = parts[1].toInt()
        return (minutes * 60 + seconds) * 1000L
    }

    private fun deleteTrack(track: Track, playlistId: Int) {
        confirmationDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track))
            .setMessage(getString(R.string.are_you_sure_delete_track))
            .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, which ->

            }
            .setPositiveButton(getString(R.string.dialog_delete)) { dialog, which ->
                viewmodel.deleteTrackFromPlaylist(track, playlistId)
            }
        confirmationDialog.show()
    }

    private fun deletePlaylist(playlistId: Int) {
        confirmationDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist))
            .setMessage(getString(R.string.are_you_sure_delete_playlist) + " «" + playlistName.text.toString() + "»?")
            .setNeutralButton(getString(R.string.no)) { dialog, which ->
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                viewmodel.deletePlaylist(playlistId)
                findNavController().navigateUp()
            }
        confirmationDialog.show()
    }

    private fun sharePlaylist() {
        BottomSheetBehavior.from(binding.menuBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
        if (!tracksList.isNullOrEmpty()) {
            viewmodel.sharePlaylist(playlist, tracksList)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.share_playlist_empty),
                Toast.LENGTH_LONG
            ).show()
        }
    }

}
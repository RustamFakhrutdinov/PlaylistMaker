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
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
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

class ChosenPlaylistFragment: Fragment() {
    private val dateFormatForMinutes by lazy { SimpleDateFormat("mm", Locale.getDefault()) }

    private lateinit var cover: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var minutesCount: TextView
    private lateinit var tracksCount:TextView
    private lateinit var confirmationDialog: MaterialAlertDialogBuilder
    private lateinit var startDrawable: Drawable

    private lateinit var playlist: Playlist
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
        binding = FragmentChosenPlaylistBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = binding.backButtonPlayer

        playlist = args.playlist
        viewmodel.loadTrackList(playlist.playlistId)
        initializeViews()
        addInformation()

        viewmodel.getTracksLiveData().observe(viewLifecycleOwner) {
            render(it)
        }

        BottomSheetBehavior.from(binding.standardBottomSheet).apply {
            binding.menuLayout.post {
                val height = resources.displayMetrics.heightPixels - binding.menuLayout.bottom -20
                if (peekHeight > height) {
                    peekHeight = height
                }
            }
        }

        //инициализируем диалог
        confirmationDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, which ->

            }
            .setPositiveButton(getString(R.string.dialog_complete)) { dialog, which ->
                findNavController().navigateUp()
            }

        //кнопка назад
        backButton.setOnClickListener {
            onBack()
        }

        //системная кнопка назад
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBack()
            }
        })

        clickDebounce = debounce<Track>(1000L, viewLifecycleOwner.lifecycleScope, false) { item ->
            val direction: NavDirections = ChosenPlaylistFragmentDirections.actionChosenPlaylistFragmentToPlayerFragment(item)
            findNavController().navigate(direction)

        }

        trackAdapter.onTrackClickListener = TrackViewHolder.OnTrackClickListener { item ->
            clickDebounce(item)
        }



    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }


    private fun initializeViews() {
        cover = binding.cover
        playlistName = binding.playlistName
        playlistDescription = binding.description
        minutesCount = binding.minutesCount
        tracksCount = binding.tracksCount
        startDrawable = binding.cover.drawable

    }
    private fun onBack() {
        findNavController().navigateUp()
    }
    private fun render(state: FavouriteTracksState) {
        when (state) {
            is FavouriteTracksState.Empty -> showEmpty(state.message)
            is FavouriteTracksState.Content ->showContent(state.tracks)
        }
    }

    private fun showEmpty(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        tracksCount.text = "0 треков"
        minutesCount.text = "0 минут"
        tracksList.clear()
        trackAdapter.notifyDataSetChanged()
    }
    private fun addInformation(){
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
    }
    private fun showContent(tracks: List<Track>) {
        tracksCount.text = playlist.count.toString() +" трек"+ getTrackWordEnding(playlist.count)

        val minutes = sumMinutes(tracks)
        minutesCount.text = dateFormatForMinutes.format(minutes) + " " + getMinuteSuffix(minutes)

        tracksListView = binding.rvTrackBSItem
        tracksListView.adapter = trackAdapter

        tracksList.clear()
        tracksList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun sumMinutes(tracks: List<Track>): Long {
        var sum: Long = 0
        tracks.forEach {track ->
            sum+=parseTimeToMillis(track.trackTime)
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

}
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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.markodevcic.peko.PermissionRequester
import com.markodevcic.peko.PermissionResult

import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediateka.domain.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.viewmodel.PlaylistViewModel
import com.practicum.playlistmaker.search.ui.SearchFragment
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewPlaylistFragment : Fragment() {

    var imageUri: Uri? = null
    private val currentDate by lazy {
        SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
            .format(Date())
    }
    private var editTextValue: String = SearchFragment.NAME_DEF
    private lateinit var nameTextWatcher: TextWatcher

    private lateinit var cover: ImageView
    private lateinit var playlistName: TextInputEditText
    private lateinit var playlistDescription: TextInputEditText
    private lateinit var createPlaylist: AppCompatButton
    private lateinit var confirmationDialog: MaterialAlertDialogBuilder
    private lateinit var startDrawable: Drawable

    private lateinit var playlist: Playlist
    private val args: NewPlaylistFragmentArgs by navArgs()


    private lateinit var binding: FragmentNewPlaylistBinding

    private val viewmodel by viewModel<PlaylistViewModel>()

    val requester = PermissionRequester.instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = binding.backButtonPlayer



        playlist = args.playlist



        initializeViews()

        //инициализируем диалог
        confirmationDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setNeutralButton(getString(R.string.dialog_cancel)) { dialog, which ->

            }
            .setPositiveButton(getString(R.string.dialog_complete)) { dialog, which ->
                findNavController().navigateUp()
            }

        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                createPlaylist.isEnabled = createButtonEnabled(s)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        nameTextWatcher?.let { playlistName.addTextChangedListener(it) }

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

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    cover.setImageURI(uri)
                    imageUri = uri
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }




        cover.setOnClickListener {
            lifecycleScope.launch {
                val permission =
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                        android.Manifest.permission.READ_MEDIA_IMAGES
                    } else {
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                requester.request(permission).collect { result ->
                    when (result) {
                        is PermissionResult.Granted -> {
                            pickMedia.launch(arrayOf("image/*"))
                        }

                        is PermissionResult.Denied.NeedsRationale -> {
                            Toast.makeText(
                                requireContext(),
                                "Для выбора изображения необходимо предоставить доступ к галерее.",
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        is PermissionResult.Denied.DeniedPermanently -> {
                            Toast.makeText(
                                requireContext(),
                                "Доступ к галерее заблокирован. Разрешите доступ в настройках приложения.",
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.data = Uri.fromParts("package", context?.packageName ?: "", null)
                            context?.startActivity(intent)
                        }


                        else -> {

                        }
                    }
                }
            }
        }

        createPlaylist.setOnClickListener {
            if (playlist.playlistId == -1) {
                createPlaylist()
                Toast.makeText(
                    requireContext(),
                    "Плейлист " + binding.enterName.text.toString() + " создан",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                editPlaylist()
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SearchFragment.SEARCH_NAME, editTextValue)
    }


    private fun initializeViews() {
        cover = binding.cover
        playlistName = binding.enterName
        playlistDescription = binding.enterDescription
        createPlaylist = binding.createButton

        if (playlist.playlistId != -1) {
            binding.newPlaylistName.text = getString(R.string.edit_playlist)
            binding.createButton.text = getString(R.string.save)
            cover.setImageURI(playlist.path?.toUri())
            //imageUri = playlist.path?.toUri()
            playlistName.setText(playlist.name)
            playlistDescription.setText(playlist.description)
            createPlaylist.isEnabled = createButtonEnabled(playlistName.text.toString())
        }
        startDrawable = binding.cover.drawable

    }

    private fun createButtonEnabled(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun onBack() {
        if (playlist.playlistId != -1) {
            findNavController().navigateUp()
        } else {
            if (!binding.enterName.text.isNullOrBlank() ||
                !binding.enterDescription.text.isNullOrBlank() ||
                binding.cover.drawable != startDrawable
            )
                confirmationDialog.show()
            else {
                findNavController().navigateUp()
            }
        }
    }

    private fun createPlaylist() {
        viewmodel.addPlaylist(
            binding.enterName.text.toString(),
            binding.enterDescription.text.toString(),
            imageUri
        )
        findNavController().navigateUp()
    }

    private fun editPlaylist() {
        viewmodel.editPlaylist(
            playlist.playlistId,
            binding.enterName.text.toString(),
            binding.enterDescription.text.toString(),
            imageUri
        )
        findNavController().navigateUp()
    }
}
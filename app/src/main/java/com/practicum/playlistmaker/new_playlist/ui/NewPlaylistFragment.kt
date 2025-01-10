package com.practicum.playlistmaker.new_playlist.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentFavoritesBinding
import com.practicum.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.practicum.playlistmaker.mediateka.ui.FavoritesFragment
import com.practicum.playlistmaker.mediateka.ui.MediatekaFragmentDirections
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragment
import com.practicum.playlistmaker.search.ui.track.TrackViewHolder
import com.practicum.playlistmaker.util.debounce
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NewPlaylistFragment: Fragment() {

    companion object {
        const val SEARCH_NAME = "TEXT_WATCHER_NAME"
        const val NAME_DEF = ""

    }
    var imageUri: Uri? = null
    private val currentDate by lazy {SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        .format(Date())}
    private var editTextValue: String = SearchFragment.NAME_DEF
    private lateinit var nameTextWatcher: TextWatcher

    private lateinit var cover: ImageView
    private lateinit var playlistName: EditText
    private lateinit var playlistDescription: EditText
    private lateinit var createPlaylist: AppCompatButton
    private lateinit var confirmationDialog: MaterialAlertDialogBuilder
    private lateinit var startDrawable: Drawable


    private lateinit var binding: FragmentNewPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentNewPlaylistBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backButton = binding.backButtonPlayer

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

        playlistName.setText(editTextValue)

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
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object: OnBackPressedCallback(true) {
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
            pickMedia.launch(arrayOf("image/*"))
        }

        createPlaylist.setOnClickListener {
            if(imageUri != null)
                saveImageToPrivateStorage(imageUri!!)
            Toast.makeText(requireContext(), "Плейлист "+ binding.enterName.text.toString() + " создан", Toast.LENGTH_LONG).show()

            findNavController().navigateUp()
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
        startDrawable = binding.cover.drawable

    }

    private fun createButtonEnabled(s: CharSequence?): Boolean {
        return !s.isNullOrEmpty()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val contentResolver = requireActivity().applicationContext.contentResolver


        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), getString(R.string.catalog))
        //создаем каталог, если он не создан
        if (!filePath.exists()){
            filePath.mkdirs()
        }

        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "playlist_image_$currentDate.jpg")

        // передаём необходимый флаг на запись
        val takeFlags: Int = Intent.FLAG_GRANT_READ_URI_PERMISSION
        contentResolver.takePersistableUriPermission(uri, takeFlags)

        // создаём входящий поток байтов из выбранной картинки
        val inputStream = contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    private fun onBack() {

        if (!binding.enterName.text.isNullOrBlank() ||
            !binding.enterDescription.text.isNullOrBlank() ||
            binding.cover.drawable != startDrawable
            )
            confirmationDialog.show()
        else{
            findNavController().navigateUp()
        }
    }
}
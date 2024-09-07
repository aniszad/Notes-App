package com.az.notes.presentation.views

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import android.transition.TransitionInflater
import android.util.Log
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.az.notes.R
import com.az.notes.databinding.ActivityEditNoteBinding
import com.az.notes.domain.Folder
import com.az.notes.domain.Note
import com.az.notes.domain.Theme
import com.az.notes.presentation.viewmodels.ViewModelEditNote
import com.az.notes.presentation.views.dialogs.MoveNotesDialogFragment
import com.az.notes.presentation.views.dialogs.MyDialogs
import com.az.notes.presentation.views.dialogs.ThemeSelectionBottomSheetFragment
import com.az.notes.utils.Constants
import com.az.notes.utils.CustomSnackBar
import com.az.notes.utils.TimestampFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.sql.Timestamp


@AndroidEntryPoint
class EditNoteActivity : BaseActivity(), MoveNotesDialogFragment.OnNotesMovedListener {

    private lateinit var binding: ActivityEditNoteBinding
    private val viewModelEditNote: ViewModelEditNote by viewModels()
    private lateinit var folder: Folder
    private val customSnackBar: CustomSnackBar by lazy {
        CustomSnackBar(binding.root, this@EditNoteActivity)
    }
    private val myDialogs : MyDialogs by lazy {
        MyDialogs(this@EditNoteActivity)
    }

    private var uiViewsIds = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditNoteBinding.inflate(layoutInflater)
        // Enable transition
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        window.sharedElementEnterTransition = TransitionInflater.from(this)
            .inflateTransition(android.R.transition.move)

        setContentView(binding.root)

        binding.apply {
            uiViewsIds = mutableListOf(
                etNoteTitle.id,
                etNoteContent.id,
                tvNoteCharNumber.id,
                tvFolderName.id,
                tvNoteDate.id
            )
        }
        getIntentExtras()


        onBackPressedDispatcher.addCallback {
            finishAfterTransition()
        }

        customizeSystemBars(binding.root.id)
        setViewModelObservers()
        setToolbarFunc()
        observeEditText()
    }

    // check if we are editing an existing note or creating a new one
    private fun getIntentExtras() {
        folder = intent.parcelable<Folder>(Constants.FOLDER_INTENT) ?: return
        val note = intent.parcelable<Note>(Constants.NOTE_INTENT)
        // setting the viewmodel note, doesn't matter if its null or not
        viewModelEditNote.setExistingNote(note)
        if (note != null) {
            viewModelEditNote.setCurrentNote(
                note
            )
        }else{
            viewModelEditNote.setCurrentNote(
                note ?: Note(
                    title = "",
                    content = "",
                    timestamp = Timestamp(System.currentTimeMillis()).time,
                    folderId = folder.folderId
                )

            )
        }


    }

    private fun setViewModelObservers() {
        viewModelEditNote.existingNote.observe(this@EditNoteActivity) { note ->
            if (note != null) {
                setupExistingNoteView(note)
            } else {
                setupNewNoteView()
            }
        }
        viewModelEditNote.isEdited.observe(this@EditNoteActivity) { isEdited ->
            if (isEdited) {
                showEditingToolbarMenu()
            } else {
                hideEditingToolbarMenu()
            }
            setThemeUi(viewModelEditNote.currentNote.value?.theme)

        }
        viewModelEditNote.createNoteResult.observe(this@EditNoteActivity) { result ->
            if (result.isSuccess) {
                onBackPressedDispatcher.onBackPressed()
            } else {
                Log.e("failed note creation", result.exceptionOrNull().toString())
                customSnackBar.launchSnackBar("failed to create note", true)
            }
        }
        viewModelEditNote.updateNoteResult.observe(this@EditNoteActivity) { result ->
            if (result.isSuccess) {
                finishAfterTransition()
                customSnackBar.launchSnackBar("note updated", true)
            } else {
                customSnackBar.launchSnackBar("failed to update note", true)
            }
        }
        viewModelEditNote.deleteNoteResult.observe(this@EditNoteActivity) { result ->
            if (result.isSuccess) {
                onBackPressedDispatcher.onBackPressed()
            } else {
                customSnackBar.launchSnackBar("failed to delete note", true)
            }
        }
        viewModelEditNote.moveNotesResult.observe(this@EditNoteActivity) { result ->
            if (result.isSuccess) {
                MoveNotesDialogFragment.dismiss(supportFragmentManager)
            } else {
                customSnackBar.launchSnackBar("failed to move note", true)
            }

        }
    }

    private fun setThemeUi(theme: Theme?) {
        val isLightTheme = theme?.isLight ?: true
        val background = theme?.let {
            ContextCompat.getDrawable(this, it.themeId)
        } ?: ContextCompat.getColor(this, R.color.colorWhiteSecondary).toDrawable()

        binding.main.background = background
        setStatusBarLight(isLightTheme)

        val textColor = if (isLightTheme) R.color.black else R.color.white

        binding.apply {
            toolbarCreateNote.navigationIcon?.let { navIcon ->
                DrawableCompat.setTint(navIcon, ContextCompat.getColor(this@EditNoteActivity, textColor))
            }

            toolbarCreateNote.menu.children.forEach {
                it.iconTintList = ColorStateList.valueOf(ContextCompat.getColor(this@EditNoteActivity, textColor))
            }

            uiViewsIds.forEach { viewId ->
                findViewById<TextView>(viewId).setTextColor(ContextCompat.getColor(this@EditNoteActivity, textColor))
            }
        }

    }

    private fun setToolbarFunc() {
        binding.toolbarCreateNote.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.toolbarCreateNote.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_validate_note -> {
                    viewModelEditNote.createOrUpdateNote()
                    true
                }

                R.id.action_delete_note -> {
                    myDialogs.showAgreeDialog(title = "Delete Note", content = "Are you sure you want to delete this note?", onConfirmed = {
                        viewModelEditNote.deleteNote()
                    })
                    true
                }

                R.id.action_share_note -> {
                    shareNote()
                    true
                }

                R.id.action_change_note_theme -> {
                    ThemeSelectionBottomSheetFragment().show(
                        supportFragmentManager,
                        "themeSelection"
                    )
                    true
                }

                R.id.action_move_note -> {
                    viewModelEditNote.existingNote.value?.let { note ->
                        lifecycleScope.launch(Dispatchers.IO) {
                            val folders = viewModelEditNote.getAllFoldersNow()
                            MoveNotesDialogFragment.show(
                                this@EditNoteActivity,
                                folders,
                                listOf(note.noteId),
                                this@EditNoteActivity
                            )
                        }
                    }

                    true
                }

                else -> false
            }
        }
    }

    private fun shareNote(){
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Note")
        shareIntent.putExtra(Intent.EXTRA_TEXT, buildString {
            append("title : ${viewModelEditNote.existingNote.value?.title ?: ""}")
            append("\n")
            append("content : ${viewModelEditNote.existingNote.value?.content ?: ""}")
        })
        startActivity(Intent.createChooser(shareIntent, "Share via"))
    }
    private fun setupExistingNoteView(note: Note) {
        setThemeUi(note.theme)
        populateNoteFields(note)
    }

    private fun populateNoteFields(note: Note) {
        with(binding) {
            etNoteTitle.setText(note.title)
            etNoteContent.setText(note.content)
            tvNoteCharNumber.text =
                getString(R.string.characters, note.title.length + note.content.length)
            tvFolderName.text = folder.name
            tvNoteDate.text = TimestampFormatter().timestampToDateAndTime(note.timestamp)
        }
    }

    private fun setupNewNoteView() {
        binding.tvNoteDate.text =
            TimestampFormatter().timestampToDateAndTime(Timestamp(System.currentTimeMillis()).time)
        binding.tvNoteCharNumber.text = buildString { append("0 characters") }
        binding.tvFolderName.text = buildString { append(folder.name) }
        binding.toolbarCreateNote.menu.removeItem(R.id.action_delete_note)
        binding.toolbarCreateNote.menu.removeItem(R.id.action_share_note)
        //binding.toolbarCreateNote.menu.removeItem(R.id.action_change_note_theme)
        binding.toolbarCreateNote.menu.removeItem(R.id.action_move_note)
    }

    private fun observeEditText() {
        binding.apply {
            etNoteContent.addTextChangedListener { text ->
                updateLengthCounter()
                viewModelEditNote.updateCurrentNote(content = text.toString())
            }
            etNoteTitle.addTextChangedListener { text ->
                updateLengthCounter()
                viewModelEditNote.updateCurrentNote(title = text.toString())
            }
        }
    }

    private fun updateLengthCounter() {
        with(binding) {
            tvNoteCharNumber.text = getString(
                R.string.characters,
                (etNoteContent.text?.length ?: 0) + (etNoteTitle.text?.length ?: 0)
            )
        }
    }

    private fun hideEditingToolbarMenu() {
        if (viewModelEditNote.existingNote.value == null) {
            binding.toolbarCreateNote.menu.clear()
            binding.toolbarCreateNote.inflateMenu(R.menu.menu_note_theme)
        } else {
            binding.toolbarCreateNote.menu.clear()
            binding.toolbarCreateNote.inflateMenu(R.menu.menu_create_note_toolbar)
        }
    }

    private fun showEditingToolbarMenu() {
        binding.toolbarCreateNote.menu.clear()
        binding.toolbarCreateNote.inflateMenu(R.menu.menu_edit_note_toolbar)
    }


    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    override fun onNotesMoved(targetFolder: Folder, notesIds: List<Int>) {
        Toast.makeText(this, "moving notes", Toast.LENGTH_SHORT).show()
        viewModelEditNote.moveNotes(notesIds, targetFolder.folderId)
    }


}
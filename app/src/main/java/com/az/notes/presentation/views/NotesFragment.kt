package com.az.notes.presentation.views

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.az.notes.R
import com.az.notes.databinding.FragmentNotesBinding
import com.az.notes.domain.Folder
import com.az.notes.domain.Note
import com.az.notes.domain.interfaces.OnFolderClickListener
import com.az.notes.domain.interfaces.OnLongClickListener
import com.az.notes.domain.interfaces.OnNoteClickListener
import com.az.notes.presentation.adapters.FolderNamesRvAdapter
import com.az.notes.presentation.adapters.NotesRvAdapter
import com.az.notes.presentation.utils.ViewAnimator
import com.az.notes.presentation.viewmodels.ViewModelMain
import com.az.notes.presentation.views.dialogs.MoveNotesDialogFragment
import com.az.notes.presentation.views.dialogs.MyDialogs
import com.az.notes.utils.Constants
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class NotesFragment : Fragment(), OnNoteClickListener, OnFolderClickListener,
    OnLongClickListener, MoveNotesDialogFragment.OnNotesMovedListener {
    private lateinit var binding: FragmentNotesBinding
    private lateinit var foldersAdapter: FolderNamesRvAdapter
    private lateinit var notesAdapter: NotesRvAdapter
    private val viewModelMain: ViewModelMain by activityViewModels()
    private val myDialogs : MyDialogs by lazy { MyDialogs(requireContext()) }
    override fun onNoteClicked(note: Note, cardNoteMain: MaterialCardView) {
        Toast.makeText(requireContext(), "n${note.noteId}", Toast.LENGTH_SHORT).show()
        val intent = Intent(context, EditNoteActivity::class.java)
        intent.putExtra(Constants.NOTE_INTENT, note)
        intent.putExtra(Constants.FOLDER_INTENT, foldersAdapter.getSelectedFolder())
        // shared element transition
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
            context as Activity,
            cardNoteMain,
            "note_to_activity_transition"
        )
        requireContext().startActivity(intent, options.toBundle())
    }

    override fun onFolderClicked(folderId: Int) {
        viewModelMain.setSelectedFolderId(folderId)
    }

    override fun onLongClick() {
        viewModelMain.setSelectionMode(true)
    }

    override fun onNotesMoved(targetFolder: Folder, notesIds: List<Int>) {
        viewModelMain.moveNotes(notesIds, targetFolder.folderId)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentNotesBinding.inflate(inflater, container, false)
        return (binding.root)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUiFunc()
        setSearchBar()
        setSelectionModeBottomAppBarFunc()
        setViewModelObservers()

        setCurrentView()
    }

    override fun onStart() {
        super.onStart()
        if (viewModelMain.searchBarHasFocus.value == false) {
            setCurrentView()
        }
    }

    private fun setSelectionModeBottomAppBarFunc() {
        binding.bottomAppBarSelectionMode.setNavigationOnClickListener {
            viewModelMain.setSelectionMode(false)
        }
        binding.bottomAppBarSelectionMode.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_select_all -> {
                    notesAdapter.selectOrDeselectAll()
                    true
                }

                R.id.action_move_selection -> {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val folders = viewModelMain.getAllFoldersNow()
                        MoveNotesDialogFragment.show(
                            this@NotesFragment,
                            folders,
                            notesAdapter.getSelectedItems(),
                            this@NotesFragment
                        )
                    }
                    true
                }
                R.id.action_pin_selection ->{
                    viewModelMain.pinNotes(notesAdapter.getSelectedItems())
                    true
                }

                R.id.action_delete_selection -> {
                    myDialogs.showAgreeDialog("Delete Notes", "Are you sure you want to delete these notes?", onConfirmed = {
                        viewModelMain.deleteNotes(notesAdapter.getSelectedItems())
                    })
                    true
                }

                else -> false
            }
        }
    }

    private fun setCurrentView() {
        // this triggers the selectedFolderId observer from which we get the notes
        viewModelMain.getFolderIdFromDb()

    }

    private fun setNotesAdapter(notesList: List<Note>) {
        if (notesList.isEmpty()) {
            binding.rvNotes.visibility = View.GONE
            binding.tvNoNotes.visibility = View.VISIBLE
        } else {
            binding.rvNotes.visibility = View.VISIBLE
            binding.tvNoNotes.visibility = View.GONE
        }
        if (::notesAdapter.isInitialized) {
            notesAdapter.updateNotes(notesList)
        }else{
            notesAdapter = NotesRvAdapter(requireContext())
            notesAdapter.setRecyclerView(binding.rvNotes)
            notesAdapter.setOnNoteClickListener(this@NotesFragment)
            notesAdapter.setOnLongClickListener(this@NotesFragment)
            val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            binding.rvNotes.layoutManager = layoutManager
            binding.rvNotes.adapter = notesAdapter
            notesAdapter.updateNotes(notesList)
            //binding.rvNotes.itemAnimator = CustomItemAnimator()
        }

    }

    private fun setViewModelObservers() {
        viewModelMain.selectedFolderId.observe(viewLifecycleOwner) { folderId ->
            viewModelMain.getFolders()
            viewModelMain.getFolderNotes(folderId)
        }
        viewModelMain.searchBarHasFocus.observe(viewLifecycleOwner) { hasFocus ->
            if (hasFocus) {
                ViewAnimator.shrinkView(binding.clFolders)
            }else{
                binding.etSearchBar.clearFocus()
                binding.etSearchBar.text?.clear()
                ViewAnimator.expandView(binding.clFolders)
                setCurrentView()
            }
        }
        viewModelMain.notes.observe(viewLifecycleOwner) { notesList ->
            setNotesAdapter(notesList)
        }
        viewModelMain.folders.observe(viewLifecycleOwner) { foldersList ->
            setFoldersAdapter(foldersList)
        }
        // creating default All and uncategorized folders
        viewModelMain.uncFolderCreationResult.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) Toast.makeText(requireContext(), "critical error", Toast.LENGTH_SHORT).show()
        }
        viewModelMain.noteSelectionMode.observe(viewLifecycleOwner) { selectionMode ->
            if (selectionMode) {
                showSelectionModeToolbar()
            } else {
                notesAdapter.closeSelectionMode()
                hideSelectionModeToolbar()
            }
        }
        viewModelMain.deleteNotesResult.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) {
                Toast.makeText(requireContext(), "Notes not deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "deleted", Toast.LENGTH_SHORT).show()
                viewModelMain.setSelectionMode(false)
                notesAdapter.updateDeletedNotes(result.getOrNull() ?: emptySet<Int>())
                setCurrentView()
            }
        }
        viewModelMain.moveNotesResult.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) {
                Toast.makeText(requireContext(), "Notes not moved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "moved", Toast.LENGTH_SHORT).show()
                viewModelMain.setSelectionMode(false)
                setCurrentView()
            }
        }
        viewModelMain.pinNotesResult.observe(viewLifecycleOwner) { result ->
            if (result.isFailure) {
                Toast.makeText(requireContext(), "Notes not pinned", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "pinned", Toast.LENGTH_SHORT).show()
                viewModelMain.setSelectionMode(false)
                setCurrentView()
            }
        }
        viewModelMain.searchNoteResult.observe(viewLifecycleOwner){
            notesAdapter.updateNotes(it)
        }
    }

    private fun showSelectionModeToolbar() {
        binding.bottomAppBarSelectionMode.visibility = View.VISIBLE
        binding.btnAddNote.visibility = View.GONE
    }

    private fun hideSelectionModeToolbar() {
        binding.bottomAppBarSelectionMode.visibility = View.GONE
        binding.btnAddNote.visibility = View.VISIBLE
    }

    private fun setFoldersAdapter(folderList: List<Folder>) {
        if (::foldersAdapter.isInitialized) {
            foldersAdapter.updateFolders(folderList, viewModelMain.selectedFolderId.value)
        }else{
            foldersAdapter = FolderNamesRvAdapter(
                folderList.indexOfFirst { folder ->
                    folder.folderId == viewModelMain.selectedFolderId.value
                })
            foldersAdapter.updateFolders(folderList)
            foldersAdapter.setOnFolderClickListener(this@NotesFragment)
            binding.rvFolders.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            binding.rvFolders.adapter = foldersAdapter
        }

    }

    private fun setSearchBar() {
        binding.etSearchBar.apply {
            addTextChangedListener { editable ->
                if (editable.toString()
                        .isNotBlank() && typeface != Typeface.DEFAULT_BOLD
                ) {
                    typeface = Typeface.DEFAULT_BOLD
                    binding.btnCloseSearchBar.visibility = View.VISIBLE
                    viewModelMain.searchNote(editable.toString())
                } else if (editable.toString().isBlank() && typeface != Typeface.DEFAULT
                ) {
                    setCurrentView()
                    typeface = Typeface.DEFAULT
                    binding.btnCloseSearchBar.visibility = View.INVISIBLE

                }
            }
        }
        binding.btnCloseSearchBar.setOnClickListener {
            binding.etSearchBar.text?.clear()
            binding.etSearchBar.clearFocus()
            viewModelMain.searchBarHasFocus(false)
        }
        binding.etSearchBar.setOnFocusChangeListener { _, hasFocus ->
            viewModelMain.searchBarHasFocus(hasFocus)
        }
    }

    private fun setUiFunc() {
        binding.apply {
            btnAddNote.setOnClickListener {
                val intent = Intent(requireActivity(), EditNoteActivity::class.java)
                intent.putExtra(Constants.FOLDER_INTENT, foldersAdapter.getSelectedFolder())
                startActivity(intent)
            }
            btnFolders.setOnClickListener {
                startActivity(Intent(requireActivity(), FoldersActivity::class.java))
            }
        }
    }

}
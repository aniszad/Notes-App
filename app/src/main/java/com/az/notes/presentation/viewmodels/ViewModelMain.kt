package com.az.notes.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.notes.data.local.MySharedPreferences
import com.az.notes.data.repository.FolderRepository
import com.az.notes.data.repository.NoteRepository
import com.az.notes.domain.Folder
import com.az.notes.domain.Note
import com.az.notes.domain.usecases.CreateUncategorizedFolderUseCase
import com.az.notes.domain.usecases.DeleteMultipleNotesUseCase
import com.az.notes.domain.usecases.MoveMultipleNotesUseCase
import com.az.notes.domain.usecases.PinMultipleNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ViewModelMain @Inject constructor(
    private var noteRepository: NoteRepository,
    private var folderRepository: FolderRepository,
    private var createUncategorizedFolderUseCase: CreateUncategorizedFolderUseCase,
    private var deleteMultipleNotesUseCase: DeleteMultipleNotesUseCase,
    private var moveMultipleNotesUseCase: MoveMultipleNotesUseCase,
    private var pinMultipleNotesUseCase: PinMultipleNotesUseCase,
    private var mySharedPreferences: MySharedPreferences
) : ViewModel() {


    private var _searchBarHasFocus: MutableLiveData<Boolean> = MutableLiveData(false)
    val searchBarHasFocus: LiveData<Boolean> get() = _searchBarHasFocus

    private var _uncFolderCreationResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val uncFolderCreationResult: LiveData<Result<Boolean>> get() = _uncFolderCreationResult

    private var _deleteNotesResult: MutableLiveData<Result<Set<Int>>> = MutableLiveData()
    val deleteNotesResult: LiveData<Result<Set<Int>>> get() = _deleteNotesResult

    private var _moveNotesResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val moveNotesResult: LiveData<Result<Boolean>> get() = _moveNotesResult

    private var _pinNotesResult: MutableLiveData<Result<Set<Int>>> = MutableLiveData()
    val pinNotesResult: LiveData<Result<Set<Int>>> get() = _pinNotesResult

    private var _noteSelectionMode: MutableLiveData<Boolean> = MutableLiveData()
    val noteSelectionMode: LiveData<Boolean> get() = _noteSelectionMode

    private var _notes: MutableLiveData<List<Note>> = MutableLiveData()
    val notes: LiveData<List<Note>> get() = _notes

    private var _folders: MutableLiveData<List<Folder>> = MutableLiveData()
    val folders: LiveData<List<Folder>> get() = _folders


    private var _searchNoteResult: MutableLiveData<List<Note>> = MutableLiveData()
    val searchNoteResult: LiveData<List<Note>> get() = _searchNoteResult

    private var _selectedFolderId: MutableLiveData<Int> = MutableLiveData()
    val selectedFolderId: LiveData<Int> get() = _selectedFolderId


    fun searchBarHasFocus(searchBarHasFocus: Boolean) {
        _searchBarHasFocus.postValue(searchBarHasFocus)
    }

    fun getUncategorizedFolder() = viewModelScope.launch(Dispatchers.IO) {
        _uncFolderCreationResult.postValue(createUncategorizedFolderUseCase.invoke())
    }

    fun getFolders() = viewModelScope.launch(Dispatchers.IO) {
        _folders.postValue(folderRepository.getAllFolders())
    }

    fun getFolderNotes(folderId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _notes.postValue(if (folderId == 1) {
            noteRepository.getAllNotes()
        } else {
            noteRepository.getFolderNotes(folderId)
        })
    }


    fun setSelectionMode(isSelecting: Boolean) {
        _noteSelectionMode.postValue(isSelecting)
    }

    fun deleteNotes(selectedNotes: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        _deleteNotesResult.postValue(deleteMultipleNotesUseCase.invoke(selectedNotes))
    }

    fun moveNotes(selectedItems: List<Int>, folderId: Int) =
        viewModelScope.launch(Dispatchers.IO) {
            _moveNotesResult.postValue(moveMultipleNotesUseCase.invoke(selectedItems, folderId))
        }

    suspend fun getAllFoldersNow(): List<Folder> = withContext(Dispatchers.IO) {
        folderRepository.getAllFoldersNow()
    }

    fun getSelectedFolder(): Int {
        return mySharedPreferences.getSelectedFolder()
    }

    private fun saveSelectedFolder(folderId: Int) {
        mySharedPreferences.saveSelectedFolder(folderId)
    }

    fun searchNote(noteText: String) = viewModelScope.launch(Dispatchers.IO) {
        _searchNoteResult.postValue(noteRepository.searchNotes(noteText))
    }

    fun getFolderIdFromDb() = viewModelScope.launch(Dispatchers.IO) {
        _selectedFolderId.postValue(mySharedPreferences.getSelectedFolder())
    }

    fun setSelectedFolderId(folderId: Int) = viewModelScope.launch(Dispatchers.IO) {
        saveSelectedFolder(folderId)
        _selectedFolderId.postValue(folderId)
    }

    fun pinNotes(notesIds: List<Int>) = viewModelScope.launch(Dispatchers.IO) {
        _pinNotesResult.postValue(pinMultipleNotesUseCase.invoke(notesIds))
    }

}
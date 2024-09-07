package com.az.notes.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.notes.data.local.MySharedPreferences
import com.az.notes.data.repository.FolderRepository
import com.az.notes.data.repository.NoteRepository
import com.az.notes.domain.Folder
import com.az.notes.domain.Note
import com.az.notes.domain.Theme
import com.az.notes.domain.usecases.CreateNoteUseCase
import com.az.notes.domain.usecases.DeleteNoteUseCase
import com.az.notes.domain.usecases.MoveMultipleNotesUseCase
import com.az.notes.domain.usecases.UpdateNoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ViewModelEditNote @Inject constructor(
    private val noteRepository: NoteRepository,
    private val folderRepository: FolderRepository,
    private val createNoteUseCase: CreateNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase,
    private val updateNoteUseCase: UpdateNoteUseCase,
    private val moveMultipleNotesUseCase: MoveMultipleNotesUseCase,
    private val mySharedPreferences: MySharedPreferences
) : ViewModel() {

    private var _createNoteResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val createNoteResult: LiveData<Result<Boolean>> get() = _createNoteResult

    private var _updateNoteResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val updateNoteResult: LiveData<Result<Boolean>> get() = _updateNoteResult

    private var _deleteNoteResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val deleteNoteResult: LiveData<Result<Boolean>> get() = _deleteNoteResult

    private var _moveNotesResult: MutableLiveData<Result<Boolean>> = MutableLiveData()
    val moveNotesResult: LiveData<Result<Boolean>> get() = _moveNotesResult

    private var _folders : MutableLiveData<List<Folder>> = MutableLiveData()
    val folders : LiveData<List<Folder>> get() =  _folders

    private var _noteTheme : MutableLiveData<Theme?> = MutableLiveData()
    val noteTheme : LiveData<Theme?> get() = _noteTheme

    private var _existingNote : MutableLiveData<Note?> = MutableLiveData()
    val existingNote : LiveData<Note?> get() = _existingNote

    private var _currentNote : MutableLiveData<Note?> = MutableLiveData()
    val currentNote : LiveData<Note?> get() = _currentNote


    // To check if there's any update for submitting
    private val _isEdited = MediatorLiveData<Boolean>()
    val isEdited: LiveData<Boolean> get() = _isEdited

    init {
        _isEdited.addSource(_existingNote) { updateIsEdited() }
        _isEdited.addSource(_currentNote) { updateIsEdited() }
    }
    private fun updateIsEdited() {
        val current = _currentNote.value
        val existing = _existingNote.value
        _isEdited.value = if (existing != null) {
            Log.e("updateIsEdited1", "current: $current, existing: $existing")
            existing != current
        } else {
            Log.e("updateIsEdited2", "current: $current, existing: $existing, isEdited:${current?.title != "" || current.content != ""}")
            current?.title != "" || current.content != ""
        }
    }

    private fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        _updateNoteResult.postValue(updateNoteUseCase.invoke(note))
    }

    fun deleteNote() = viewModelScope.launch(Dispatchers.IO) {
        _deleteNoteResult.postValue(deleteNoteUseCase.invoke(existingNote.value?.noteId))
    }

    private fun createNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        _createNoteResult.postValue(createNoteUseCase.invoke(note))
    }

    suspend fun getAllFoldersNow(): List<Folder> = withContext(Dispatchers.IO) {
        folderRepository.getAllFoldersNow()
    }
    fun moveNotes(note: List<Int>, checkedRadioButtonId: Int)= viewModelScope.launch(Dispatchers.IO){
        _moveNotesResult.postValue(moveMultipleNotesUseCase.invoke(note, checkedRadioButtonId))
    }

    private fun setTheme(theme : Theme?) {
        _noteTheme.postValue(theme)
    }

    fun setExistingNote(note: Note?) {
        _existingNote.postValue(note)
    }

    fun updateCurrentNote(title: String? = null, content: String? = null, folderId: Int? = null, theme: Theme? = null) {
        setTheme(theme)
        val note = currentNote.value
        Log.e("currentInfo", "currentNote: ${note},")

        note?.let {
            val updatedNote = it.copy(
                title = title ?: it.title,
                content = content ?: it.content,
                folderId = folderId ?: it.folderId,
                theme = theme
            )
            if (updatedNote != it) {
                _currentNote.postValue(updatedNote)
                Log.e("currentInfo", "updated: ${updatedNote}")
            }
        }
    }


    fun createOrUpdateNote() {
        val note = currentNote.value
        Log.e("createOrUpdateNote", "note: $note")
        note?.let {
            if (existingNote.value == null) {
                createNote(it)
            } else {
                updateNote(it)
            }
        }
    }

    fun setCurrentNote(note: Note) {
        _currentNote.postValue(note)
    }

}
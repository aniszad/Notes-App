package com.az.notes.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.notes.data.local.MySharedPreferences
import com.az.notes.data.repository.FolderRepository
import com.az.notes.domain.Folder
import com.az.notes.domain.usecases.CreateFolderUseCase
import com.az.notes.domain.usecases.DeleteMultipleFoldersUseCase
import com.az.notes.domain.usecases.PinMultipleFoldersUseCase
import com.az.notes.domain.usecases.UpdateFolderNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewModelFolders @Inject constructor(
    private val folderRepository: FolderRepository,
    private val createFolderUseCase: CreateFolderUseCase,
    private val deleteMultipleFoldersUseCase: DeleteMultipleFoldersUseCase,
    private val pinMultipleFoldersUseCase: PinMultipleFoldersUseCase,
    private val updateFolderNameUseCase: UpdateFolderNameUseCase,
    private val mySharedPreferences: MySharedPreferences
) : ViewModel() {


    private var _folders : MutableLiveData<List<Folder>> = MutableLiveData()
    val folders : LiveData<List<Folder>> get() =  _folders

    private var _selectedFoldersIds : MutableLiveData<Set<Int>> = MutableLiveData()
    val selectedFoldersIds : LiveData<Set<Int>> get() =  _selectedFoldersIds

    private var _createFolderResult : MutableLiveData<Result<Folder>> = MutableLiveData()
    val createFolderResult : LiveData<Result<Folder>> get() =  _createFolderResult

    private var _pinFoldersResult : MutableLiveData<Result<Set<Int>>> = MutableLiveData()
    val pinFolderResult : LiveData<Result<Set<Int>>> get() =  _pinFoldersResult

    private var _deleteFoldersResult : MutableLiveData<Result<Set<Int>>> = MutableLiveData()
    val deleteFoldersResult : LiveData<Result<Set<Int>>> get() =  _deleteFoldersResult

    private var _updateFolderNameResult : MutableLiveData<Result<Boolean>> = MutableLiveData()
    val updateFolderNameResult : LiveData<Result<Boolean>> get() =  _updateFolderNameResult

    private var _selectionMode : MutableLiveData<Boolean> = MutableLiveData()
    val selectionMode : LiveData<Boolean> get() =  _selectionMode

    private var _selectedFolderId : MutableLiveData<Int> = MutableLiveData()
    private val selectedFolderId : LiveData<Int> get() =  _selectedFolderId


    fun getAllFolders() = viewModelScope.launch(Dispatchers.IO) {
        _folders.postValue(folderRepository.getAllFolders())
    }

    fun createFolder(folder: Folder) = viewModelScope.launch(Dispatchers.IO) {
        _createFolderResult.postValue(createFolderUseCase.invoke(folder))
    }
    fun setSelectedFolderId(folderId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _selectedFolderId.postValue(folderId)
        mySharedPreferences.saveSelectedFolder(folderId)
    }

    fun setSelectionMode(inSelectionMode: Boolean) {
        _selectionMode.postValue(inSelectionMode)
    }

    fun pinFolders(selectedFoldersIds: MutableSet<Int>)  = viewModelScope.launch(Dispatchers.IO){
        _pinFoldersResult.postValue(pinMultipleFoldersUseCase.invoke(selectedFoldersIds))
    }

    fun deleteFolders(selectedFoldersIds: Set<Int>)  = viewModelScope.launch(Dispatchers.IO){
        _deleteFoldersResult.postValue(deleteMultipleFoldersUseCase.invoke(selectedFoldersIds))
        if (selectedFolderId.value in selectedFoldersIds){
            setSelectedFolderId(1)
        }
    }

    fun updateSelectedFolderIds(folderId: Int) {
        if (selectedFoldersIds.value?.contains(folderId) == true) {
            val newSelectedFoldersIds = selectedFoldersIds.value?.toMutableSet() ?: mutableSetOf()
            newSelectedFoldersIds.remove(folderId)
            _selectedFoldersIds.postValue(newSelectedFoldersIds)
        } else {
            val newSelectedFoldersIds = selectedFoldersIds.value?.toMutableSet() ?: mutableSetOf()
            newSelectedFoldersIds.add(folderId)
            _selectedFoldersIds.postValue(newSelectedFoldersIds)
        }
    }

    fun updateFolderName(first: Int, folderName: String) = viewModelScope.launch {
        _updateFolderNameResult.postValue(updateFolderNameUseCase.invoke(first, folderName))
    }


}
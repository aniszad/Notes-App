package com.az.notes.presentation.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.az.notes.data.repository.TaskRepository
import com.az.notes.domain.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ViewModelTasks @Inject constructor(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private var _finishedTasks: MutableLiveData<List<Task>> = MutableLiveData()
    val finishedTasks: MutableLiveData<List<Task>> get() = _finishedTasks

    private var _unfinishedTasks: MutableLiveData<List<Task>> = MutableLiveData()
    val unfinishedTasks: MutableLiveData<List<Task>> get() = _unfinishedTasks

    private var _updateTaskStatusResult: MutableLiveData<Result<Int>> = MutableLiveData()
    val updateTaskStatusResult: MutableLiveData<Result<Int>> get() = _updateTaskStatusResult

    private var _createTaskResult: MutableLiveData<Result<Int>> = MutableLiveData()
    val createTaskResult: MutableLiveData<Result<Int>> get() = _createTaskResult

    private var _deleteTaskResult: MutableLiveData<Result<Int>> = MutableLiveData()
    val deleteTaskResult: MutableLiveData<Result<Int>> get() = _deleteTaskResult


    fun updateTaskStatus(task: Int, isChecked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        _updateTaskStatusResult.postValue(taskRepository.updateTaskStatus(task, isChecked))
    }

    fun getFinishedTasks() = viewModelScope.launch(Dispatchers.IO) {
        _finishedTasks.postValue(taskRepository.getFinishedTasks())

    }

    fun getUnfinishedTasks() = viewModelScope.launch(Dispatchers.IO) {
        _unfinishedTasks.postValue(taskRepository.getUnfinishedTasks())
    }

    fun deleteTask(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        _deleteTaskResult.postValue(taskRepository.deleteTask(taskId))
    }

    fun createTask(task: Task) = viewModelScope.launch(Dispatchers.IO){
        _createTaskResult.postValue(taskRepository.createTask(task))
    }
}



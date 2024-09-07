package com.az.notes.data.repository

import com.az.notes.data.dao.TaskDao
import com.az.notes.domain.Task
import javax.inject.Inject


class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks()
    }

    suspend fun updateTaskStatus(taskId: Int, isChecked: Boolean): Result<Int> {
        return try {
            taskDao.toggleDoneTaskStatus(taskId, isChecked)
            Result.success(taskId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun delete(taskId: Int) {
        taskDao.delete(taskId)
    }

    suspend fun searchTasks(taskText: String): List<Task>? {
        return taskDao.searchTasks(taskText)
    }

    suspend fun getFinishedTasks(): List<Task>? {
        return taskDao.getFinishedTasks()
    }

    suspend fun getUnfinishedTasks(): List<Task>? {
        return taskDao.getUnfinishedTasks()
    }

    suspend fun deleteTask(taskId: Int) : Result<Int> {
        return try {
            taskDao.delete(taskId)
            Result.success(taskId)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    suspend fun createTask(task: Task): Result<Int>? {
        return try {
            taskDao.insert(task)
            Result.success(task.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

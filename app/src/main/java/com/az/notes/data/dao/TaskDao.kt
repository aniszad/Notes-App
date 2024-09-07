package com.az.notes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.az.notes.domain.Task

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun delete(taskId: Int)

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Int): Task?

    @Query("SELECT * FROM tasks WHERE content LIKE :taskText")
    suspend fun searchTasks(taskText: String): List<Task>?

    @Query("UPDATE tasks SET done = :isChecked WHERE id = :taskId")
    suspend fun toggleDoneTaskStatus(taskId: Int, isChecked: Boolean)

    @Query("SELECT * FROM tasks WHERE done = 1")
    suspend fun getFinishedTasks(): List<Task>?

    @Query("SELECT * FROM tasks WHERE done = 0")
    suspend fun getUnfinishedTasks(): List<Task>?
}

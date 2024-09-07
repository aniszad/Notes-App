package com.az.notes.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.az.notes.databinding.TaskItemLayoutBinding
import com.az.notes.domain.Task
import com.az.notes.domain.interfaces.DeleteTaskListener
import com.az.notes.domain.interfaces.UpdateTaskStatusListener
import com.az.notes.utils.TimestampFormatter

class TaskRvAdapter(
) : RecyclerView.Adapter<TaskRvAdapter.TaskViewHolder>() {
    private var updateTaskStatus: UpdateTaskStatusListener? = null
    private var deleteTaskListener: DeleteTaskListener? = null
    private var tasksList: MutableList<Task> = mutableListOf()


    class TaskViewHolder(binding: TaskItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        val taskContent = binding.tvTaskText
        val cbTaskDone = binding.cbTaskDone
        val btnDeleteTask = binding.btnDeleteTask
        val tvTaskDate = binding.tvTaskDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(
            TaskItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return tasksList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasksList[position]
        setupCheckboxListener(holder, currentTask)
        holder.taskContent.text = currentTask.content
        holder.taskContent.setTextColor(if (currentTask.done) holder.taskContent.context.getColor(android.R.color.darker_gray) else holder.taskContent.context.getColor(android.R.color.black))
        holder.tvTaskDate.setTextColor(if (currentTask.done) holder.taskContent.context.getColor(android.R.color.darker_gray) else holder.taskContent.context.getColor(android.R.color.black))
        holder.tvTaskDate.text = TimestampFormatter().timestampToDateAndTime(currentTask.timestamp)

        holder.btnDeleteTask.setOnClickListener {
            deleteTaskListener?.deleteTask(currentTask.id)
        }


    }

    private fun setupCheckboxListener(holder: TaskViewHolder, task: Task) {
        holder.cbTaskDone.setOnCheckedChangeListener(null) // Remove old listener
        holder.cbTaskDone.isChecked = task.done
        holder.cbTaskDone.setOnCheckedChangeListener { _, isChecked ->
            updateTaskStatus?.updateTaskStatus(task.id, isChecked)
        }
    }
    fun setDeleteTaskListener(deleteTaskListener: DeleteTaskListener) {
        this.deleteTaskListener = deleteTaskListener
    }
    fun setUpdateStatusListener(updateTaskStatus: UpdateTaskStatusListener) {
        this.updateTaskStatus = updateTaskStatus
    }

    fun updateElementCheckStatus(){

    }

    fun updateDataset(newTasksList: List<Task>) {
        if (this.tasksList.size == newTasksList.size + 1) {
            // Potential single element deletion
            val deletedIndex = findDeletedIndex(this.tasksList, newTasksList)
            if (deletedIndex != -1) {
                this.tasksList.removeAt(deletedIndex)
                notifyItemRemoved(deletedIndex)
                return
            }
        } else if (this.tasksList.size == newTasksList.size) {
            // Potential single element change
            val changedIndex = findChangedIndex(this.tasksList, newTasksList)
            if (changedIndex != -1) {
                this.tasksList[changedIndex] = newTasksList[changedIndex]
                notifyItemChanged(changedIndex)
                return
            }
        }

        // If we reach here, it's either an addition or multiple changes
        this.tasksList = newTasksList.toMutableList()
        this.tasksList.sortByDescending { it.timestamp }
        notifyDataSetChanged()
    }

    private fun findDeletedIndex(oldList: List<Task>, newList: List<Task>): Int {
        for (i in oldList.indices) {
            if (i >= newList.size || oldList[i] != newList[i]) {
                return i
            }
        }
        return -1
    }

    private fun findChangedIndex(oldList: List<Task>, newList: List<Task>): Int {
        for (i in oldList.indices) {
            if (oldList[i] != newList[i]) {
                return i
            }
        }
        return -1
    }

    fun updateElementDeleted(){

    }
}
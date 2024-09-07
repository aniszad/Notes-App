package com.az.notes.domain.interfaces

interface UpdateTaskStatusListener{
        fun updateTaskStatus(taskId : Int, isChecked: Boolean)
}


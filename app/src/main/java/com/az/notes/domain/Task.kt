package com.az.notes.domain

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val done: Boolean,
    val timestamp: Long
)
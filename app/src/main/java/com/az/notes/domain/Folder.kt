package com.az.notes.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(
    tableName = "folders",
    indices = [Index(value = ["name"], unique = true)]
)
data class Folder(
    @PrimaryKey(autoGenerate = true) val folderId: Int = 0,
    val name: String,
    var isPinned : Boolean = false,
    val pinnedAt : Long? = null,
    val timestamp: Long
) : Parcelable
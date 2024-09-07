package com.az.notes.domain

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.az.notes.domain.converters.ThemeConverter
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "notes",
    foreignKeys = [ForeignKey(
        entity = Folder::class,
        parentColumns = ["folderId"],
        childColumns = ["folderId"],
        onDelete = ForeignKey.CASCADE
    )]
)
@TypeConverters(ThemeConverter::class)
data class Note(
    @PrimaryKey(autoGenerate = true) val noteId: Int = 0,
    val title: String,
    val content: String,
    val isPinned: Boolean = false,
    val isPinnedAt: Long? = null,
    val timestamp: Long,
    var folderId: Int = 0,
    var theme: Theme? = null
) : Parcelable
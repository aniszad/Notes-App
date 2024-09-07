package com.az.notes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.az.notes.domain.Note
import com.az.notes.domain.Theme

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note:Note)

    @Update
    suspend fun update(note: Note)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun delete(noteId: Int)

    @Query("SELECT * FROM notes")
    suspend fun getAllNotes(): List<Note>

    @Query("SELECT * FROM notes WHERE folderId = :id")
    suspend fun getNoteById(id: Int): Note?

    @Query("SELECT * FROM notes WHERE folderId = :folderId")
    suspend fun getFolderNotes(folderId: Int): List<Note>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :noteText || '%' OR content LIKE '%' || :noteText || '%'")
    suspend fun searchNotes(noteText: String): List<Note>?

    @Query("UPDATE notes SET theme = :theme WHERE noteId = :noteId")
    suspend fun updateNoteTheme(noteId: Int, theme: Theme)

    @Query("UPDATE notes SET folderId = :folderId WHERE noteId = :noteId")
    suspend fun moveNote(noteId: Int, folderId: Int)

    @Query("UPDATE notes SET isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END WHERE noteId = :noteId")
    suspend fun togglePinNoteStatus(noteId: Int)
}

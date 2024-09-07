package com.az.notes.data.repository

import com.az.notes.data.dao.NoteDao
import com.az.notes.domain.Note
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    suspend fun getAllNotes(): List<Note> {
        return noteDao.getAllNotes()
    }

    suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    suspend fun moveNote(noteId: Int, folderId: Int){
        return noteDao.moveNote(noteId, folderId)
    }

    suspend fun delete(noteId: Int) {
        noteDao.delete(noteId)
    }

    suspend fun getFolderNotes(folderId: Int): List<Note> {
        return noteDao.getFolderNotes(folderId)
    }

    suspend fun searchNotes(noteText: String): List<Note>? {
        return noteDao.searchNotes(noteText)
    }

    suspend fun togglePinNoteStatus(noteId: Int) {
        noteDao.togglePinNoteStatus(noteId)
    }
}

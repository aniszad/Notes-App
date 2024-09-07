package com.az.notes.domain.usecases

import com.az.notes.data.repository.NoteRepository
import com.az.notes.domain.Note
import javax.inject.Inject


class MoveNoteUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend fun invoke(note : Note, folderId : Int) : Result<Boolean> {
        return try{
            note.folderId = folderId
            noteRepository.updateNote(note)
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }

    }
}
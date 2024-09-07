package com.az.notes.domain.usecases

import com.az.notes.data.repository.NoteRepository
import javax.inject.Inject


class MoveMultipleNotesUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend fun invoke(notesIds : List<Int>, folderId : Int) : Result<Boolean> {
        return try{
            for (noteId in notesIds){
                noteRepository.moveNote(noteId, folderId)
            }
            Result.success(true)
        }catch (e: Exception){
            Result.failure(e)
        }

    }
}
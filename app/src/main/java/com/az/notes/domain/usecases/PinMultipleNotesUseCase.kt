package com.az.notes.domain.usecases

import com.az.notes.data.repository.NoteRepository
import javax.inject.Inject


class PinMultipleNotesUseCase @Inject constructor(private val noteRepository: NoteRepository) {
    suspend fun invoke(notesIds : List<Int>) : Result<Set<Int>> {
        return try{
            for (noteId in notesIds){
                noteRepository.togglePinNoteStatus(noteId)
            }
            Result.success(notesIds.toSet())
        }catch (e: Exception){
            Result.failure(e)
        }

    }
}
package com.az.notes.domain.usecases

import com.az.notes.data.repository.NoteRepository
import javax.inject.Inject

class DeleteNoteUseCase @Inject constructor(private var noteRepo: NoteRepository){

    suspend fun invoke(noteId : Int?) : Result<Boolean>{
        return try{
            if (noteId == null) throw IllegalArgumentException("The note id must not be null")
            noteRepo.delete(noteId)
            Result.success(true)
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
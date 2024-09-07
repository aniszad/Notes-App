package com.az.notes.domain.usecases

import com.az.notes.data.repository.NoteRepository
import com.az.notes.domain.Note
import javax.inject.Inject

class CreateNoteUseCase @Inject constructor(private var noteRepo: NoteRepository){

    suspend fun invoke(note : Note) : Result<Boolean>{
        return try{
            noteRepo.insert(note)
            Result.success(true)
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
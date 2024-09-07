package com.az.notes.domain.usecases

import com.az.notes.data.repository.NoteRepository
import javax.inject.Inject

class DeleteMultipleNotesUseCase @Inject constructor(private var noteRepo: NoteRepository){

    suspend fun invoke(notesList : List<Int>) : Result<Set<Int>>{
        return try{
            for (note in notesList){
                noteRepo.delete(note)
            }
            Result.success(notesList.toSet())
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
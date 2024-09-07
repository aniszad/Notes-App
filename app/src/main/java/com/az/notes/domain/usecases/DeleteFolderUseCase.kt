package com.az.notes.domain.usecases

import com.az.notes.data.repository.FolderRepository
import com.az.notes.data.repository.NoteRepository
import javax.inject.Inject

class DeleteFolderUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend fun invoke(folderId: Int): Result<Int> {
        return try {
            folderRepository.delete(folderId)
             Result.success(folderId)
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
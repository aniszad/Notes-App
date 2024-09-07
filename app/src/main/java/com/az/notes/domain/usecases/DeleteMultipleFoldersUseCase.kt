package com.az.notes.domain.usecases

import com.az.notes.data.repository.FolderRepository
import javax.inject.Inject

class DeleteMultipleFoldersUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend fun invoke(foldersIds: Set<Int>): Result<Set<Int>> {
        return try {
            for(folderId in foldersIds){
                folderRepository.delete(folderId)
            }
            Result.success(foldersIds)
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
package com.az.notes.domain.usecases

import com.az.notes.data.repository.FolderRepository
import javax.inject.Inject


class UpdateFolderNameUseCase @Inject constructor(
    private val folderRepository: FolderRepository
) {
    suspend fun invoke(folderId: Int, folderName: String): Result<Boolean> {
        return try {
            folderRepository.updateFolderName(folderId, folderName)
            Result.success(true)
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
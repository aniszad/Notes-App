package com.az.notes.domain.usecases

import com.az.notes.data.repository.FolderRepository
import com.az.notes.domain.Folder
import javax.inject.Inject

class CreateUncategorizedFolderUseCase @Inject constructor(private val folderRepo: FolderRepository) {
    suspend fun invoke(): Result<Boolean> {
        return try {
            val existingAllFolder = folderRepo.getFolderById(1)
            val existingUncategorizedFolder = folderRepo.getFolderById(2)

            if (existingAllFolder == null) {
                val allFolder = Folder(
                    folderId = 1,
                    name = "All",
                    timestamp = System.currentTimeMillis()
                )
                folderRepo.insert(allFolder)
            }

            if (existingUncategorizedFolder == null) {
                val uncategorizedFolder = Folder(
                    folderId = 2,
                    name = "Uncategorized",
                    timestamp = System.currentTimeMillis()
                )
                folderRepo.insert(uncategorizedFolder)
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}
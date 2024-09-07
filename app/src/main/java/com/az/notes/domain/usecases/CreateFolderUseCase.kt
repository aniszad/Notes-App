package com.az.notes.domain.usecases

import com.az.notes.data.repository.FolderRepository
import com.az.notes.domain.Folder
import javax.inject.Inject

class CreateFolderUseCase @Inject constructor(private var folderRepository: FolderRepository){
    suspend fun invoke(folder: Folder) : Result<Folder> {
        return try {
            folderRepository.insert(folder)
            Result.success(folder)
        }catch (e : Exception){
            Result.failure(e)
        }
    }
}
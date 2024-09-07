package com.az.notes.domain.usecases

import com.az.notes.data.repository.FolderRepository
import java.sql.Timestamp
import javax.inject.Inject


class PinMultipleFoldersUseCase @Inject constructor(private val foldersRepository: FolderRepository) {
    suspend operator fun invoke(selectedFoldersIds: MutableSet<Int>): Result<MutableSet<Int>> {
        return try {
            for (folderId in selectedFoldersIds) {
                // changes the pin status of the folder
                foldersRepository.togglePinNoteStatus(folderId, Timestamp(System.currentTimeMillis()).time)
            }
            Result.success(selectedFoldersIds)
        }catch (e : Exception){
            Result.failure(e)
        }
    }

}
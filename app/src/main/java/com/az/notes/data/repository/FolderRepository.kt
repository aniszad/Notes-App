package com.az.notes.data.repository

import com.az.notes.data.dao.FolderDao
import com.az.notes.domain.Folder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderRepository(private val folderDao: FolderDao) {

    suspend fun insert(folder: Folder) {
        withContext(Dispatchers.IO) {
            folderDao.insert(folder)
        }
    }

    suspend fun update(folder: Folder) {
        withContext(Dispatchers.IO) {
            folderDao.update(folder)
        }
    }

    suspend fun delete(folderId: Int) {
        folderDao.delete(folderId)
    }

    suspend fun getAllFolders(): List<Folder> {
        return folderDao.getAllFolders()
    }
    suspend fun getAllFoldersNow(): List<Folder> {
        return withContext(Dispatchers.IO) {
            folderDao.getAllFolders()
        }
    }

    suspend fun getFolderById(id: Int): Folder {
        return withContext(Dispatchers.IO) {
            folderDao.getFolderById(id)
        }
    }

    suspend fun togglePinNoteStatus(folderId: Int, pinnedAt: Long) {
        folderDao.togglePinNoteStatus(folderId, pinnedAt)
    }

    suspend fun updateFolderName(first: Int, folderName: String) {
        folderDao.updateFolderName(first, folderName)
    }
}

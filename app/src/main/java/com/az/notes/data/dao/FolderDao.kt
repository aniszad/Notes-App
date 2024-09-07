package com.az.notes.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.az.notes.domain.Folder

@Dao
interface FolderDao {
    @Insert
    suspend fun insert(folder: Folder)

    @Update
    suspend fun update(folder: Folder)

    @Query("DELETE FROM folders WHERE folderId = :folderId")
    suspend fun delete(folderId: Int)

    @Query("SELECT * FROM folders")
    suspend fun getAllFolders(): List<Folder>

    @Query("SELECT * FROM folders WHERE folderId = :id")
    suspend fun getFolderById(id: Int): Folder

    @Query("""
    UPDATE folders 
    SET 
        isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END, 
        pinnedAt = :pinnedAt 
    WHERE folderId = :folderId
    """)
    suspend fun togglePinNoteStatus(folderId: Int, pinnedAt: Long)

    @Query("UPDATE folders SET name = :folderName WHERE folderId = :folderId")
    suspend fun updateFolderName(folderId: Int, folderName: String)
}

package com.az.notes.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.az.notes.data.dao.FolderDao
import com.az.notes.data.dao.NoteDao
import com.az.notes.data.dao.TaskDao
import com.az.notes.domain.Folder
import com.az.notes.domain.Note
import com.az.notes.domain.Task
import com.az.notes.domain.converters.ThemeConverter

@Database(entities = [Note::class, Task::class, Folder::class], version = 11)
@TypeConverters(ThemeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun taskDao(): TaskDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

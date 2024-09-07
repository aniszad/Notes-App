package com.az.notes.di

import android.content.Context
import com.az.notes.data.dao.FolderDao
import com.az.notes.data.dao.NoteDao
import com.az.notes.data.dao.TaskDao
import com.az.notes.data.local.AppDatabase
import com.az.notes.data.local.MySharedPreferences
import com.az.notes.data.repository.FolderRepository
import com.az.notes.data.repository.NoteRepository
import com.az.notes.domain.usecases.CreateUncategorizedFolderUseCase
import com.az.notes.domain.usecases.DeleteMultipleNotesUseCase
import com.az.notes.domain.usecases.MoveMultipleNotesUseCase
import com.az.notes.domain.usecases.PinMultipleNotesUseCase
import com.az.notes.presentation.viewmodels.ViewModelMain
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ModuleApp {

    @Provides
    fun provideContext(@ApplicationContext appContext: Context): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun provideDatabase(context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideNoteDao(database: AppDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    fun provideFolderDao(database: AppDatabase): FolderDao {
        return database.folderDao()
    }

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepository(noteDao)
    }

    @Provides
    fun provideFolderRepository(folderDao: FolderDao): FolderRepository {
        return FolderRepository(folderDao)
    }

    @Provides
    fun provideCreateUncategorizedFolderUseCase(folderRepository: FolderRepository): CreateUncategorizedFolderUseCase {
        return CreateUncategorizedFolderUseCase(folderRepository)
    }

    @Provides
    fun provideMySharedPreferences(context: Context): MySharedPreferences {
        return MySharedPreferences(context)
    }

    @Provides
    fun provideNoteViewModel(
        noteRepository: NoteRepository,
        folderRepository: FolderRepository,
        createUncategorizedFolderUseCase: CreateUncategorizedFolderUseCase,
        deleteMultipleNotesUseCase: DeleteMultipleNotesUseCase,
        moveMultipleNotesUseCase: MoveMultipleNotesUseCase,
        pinMultipleNotesUseCase: PinMultipleNotesUseCase,
        mySharedPreferences: MySharedPreferences
    ): ViewModelMain {
        return ViewModelMain(
            noteRepository,
            folderRepository,
            createUncategorizedFolderUseCase,
            deleteMultipleNotesUseCase,
            moveMultipleNotesUseCase,
            pinMultipleNotesUseCase,
            mySharedPreferences
        )
    }

}
package com.az.notes.data.local

import android.content.Context
import android.content.SharedPreferences


class MySharedPreferences(context: Context) {

    companion object {
        const val SELECTED_FOLDER = "selected_folder"


        private const val PREF_NAME = "my_app_prefs"
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    private val selectedFolder = context.getSharedPreferences(SELECTED_FOLDER, Context.MODE_PRIVATE)

    private val selectedFolderEditor = selectedFolder.edit()

    fun saveSelectedFolder(folderId: Int) {
        selectedFolderEditor.putInt(SELECTED_FOLDER, folderId)
        selectedFolderEditor.apply()
    }
    fun getSelectedFolder(): Int {
        // if the key is not found, it will return 1 (which is per default the all folder)
        return selectedFolder.getInt(SELECTED_FOLDER, 1)
    }

}
package com.az.notes.domain.converters

import androidx.room.TypeConverter
import com.az.notes.domain.Theme
import com.google.gson.Gson

class ThemeConverter {
    @TypeConverter
    fun fromTheme(theme: Theme?): String? {
        return theme?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun toTheme(themeString: String?): Theme? {
        return themeString?.let { Gson().fromJson(it, Theme::class.java) }
    }
}

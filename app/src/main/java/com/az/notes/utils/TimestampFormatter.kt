package com.az.notes.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimestampFormatter (){

    fun timestampToDateAndTime(timestamp: Long): String {
        val sdf = SimpleDateFormat("MMM dd HH:mm", Locale.getDefault())
        val date = Date(timestamp)
        return sdf.format(date)
    }
}
package com.az.notes.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Theme (
    var themeId : Int,
    var isLight : Boolean,
) : Parcelable
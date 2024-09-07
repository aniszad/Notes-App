package com.az.notes.utils

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import com.az.notes.R
import com.google.android.material.snackbar.Snackbar

class CustomSnackBar(mainView:View, private val context: Context) {
    private val snackBar = Snackbar.make(mainView, "", Snackbar.LENGTH_LONG)

    init {
        snackBar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE)
        snackBar.setTextColor(
            ContextCompat.getColor(context, R.color.white)
        )
    }

    fun launchSnackBar(message:String, error:Boolean){
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(context.getColor(if (error) R.color.colorRedPrimary else R.color.colorGreenPrimary ))
        snackBar.setText(message)
        snackBar.show()
    }
}
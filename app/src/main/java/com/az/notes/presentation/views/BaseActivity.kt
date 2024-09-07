package com.az.notes.presentation.views

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

open class BaseActivity : AppCompatActivity() {
    private lateinit var mWarningDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setStatusBarLight(isLight: Boolean) {
        val decorView = window.decorView
        val wic = WindowInsetsControllerCompat(window, decorView)
        wic.isAppearanceLightStatusBars = isLight
    }

    fun setSystemBarsColors(color: Int) {
        window.statusBarColor = color
        window.navigationBarColor = color
    }

    fun setStatusBarColor(color: Int) {
        window.statusBarColor = color
    }

    fun setNavigationBarColor(color: Int) {
        window.navigationBarColor = color
    }

    fun customizeSystemBars(
        rootViewId: Int,
        statusBarColor: Int = Color.TRANSPARENT,
        navigationBarColor: Int = Color.TRANSPARENT,
        isLightStatusBar: Boolean = true
    ) {
        window.statusBarColor = statusBarColor
        window.navigationBarColor = navigationBarColor
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(rootViewId)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }
        setStatusBarLight(isLightStatusBar)
    }

}
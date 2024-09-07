package com.az.notes.presentation.utils

import android.view.View

object ViewAnimator {
    fun shrinkView(view: View, duration: Long = 300, onFinish: () -> Unit = {}) {
        view.animate()
            .scaleX(0f)
            .scaleY(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onFinish()
            }
            .start()
    }

    fun expandView(view: View, duration: Long = 300, onFinish: () -> Unit = {}) {
        view.scaleX = 0f
        view.scaleY = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(duration)
            .withEndAction {
                onFinish()
            }
            .start()
    }

    fun changeToVisibleWithAnim(view: View, duration: Long = 300, onFinish: () -> Unit = {}) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .withEndAction {
                onFinish()
            }
            .start()
    }

    fun changeToGoneWithAnim(view: View, duration: Long = 300, onFinish: () -> Unit = {}) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onFinish()
            }
            .start()
    }
}
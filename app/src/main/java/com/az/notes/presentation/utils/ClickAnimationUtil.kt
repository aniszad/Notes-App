package com.az.notes.presentation.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

object ClickAnimationUtil {
    private var animatorSet: AnimatorSet? = null

    fun setClickWithAnimation(view: View, action: () -> Unit) {
        view.setOnClickListener {
            animateClick(view)
            action()
        }
    }

    fun setLongClickWithAnimation(view: View, action: () -> Unit) {
        view.setOnLongClickListener {
            animateClick(view)
            true // Return true to indicate long click is consumed
        }

        // Reset animation on release
        view.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                reverseAnimation(v)
            }
            false
        }
    }

    private fun animateClick(view: View) {
        cancelAnimation() // Cancel any ongoing animation

        val scaleDownX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f, 1f)
        val scaleDownY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.95f, 1f)
        scaleDownX.duration = 300
        scaleDownY.duration = 300

        animatorSet = AnimatorSet()
        animatorSet?.play(scaleDownX)?.with(scaleDownY)
        animatorSet?.interpolator = AccelerateDecelerateInterpolator()
        animatorSet?.start()
    }

    private fun reverseAnimation(view: View) {
        animatorSet?.reverse()
    }

    private fun cancelAnimation() {
        animatorSet?.cancel()
    }
}



// Extension function for easier usage
fun View.setClickAnimationWithAction(action: () -> Unit) {
    ClickAnimationUtil.setClickWithAnimation(this, action)
}
fun View.setLongClickAnimationWithAction(action: () -> Unit) {
    ClickAnimationUtil.setLongClickWithAnimation(this, action)
}

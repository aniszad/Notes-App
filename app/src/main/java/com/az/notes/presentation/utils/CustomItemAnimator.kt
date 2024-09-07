package com.az.notes.presentation.utils

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import android.view.animation.AnimationUtils
import android.view.animation.Animation

class CustomItemAnimator : SimpleItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.itemView?.let { view ->
            view.alpha = 0f
            view.scaleX = 0.5f
            view.scaleY = 0.5f

            view.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(300).start()
        }
        return true
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        holder?.itemView?.let { view ->
            view.animate().alpha(0f).scaleX(0.5f).scaleY(0.5f).setDuration(300)
                .withEndAction { dispatchRemoveFinished(holder) }
                .start()
        }
        return true
    }

    // Implement other required methods
    override fun animateChange(oldHolder: RecyclerView.ViewHolder?, newHolder: RecyclerView.ViewHolder?, fromLeft: Int, fromTop: Int, toLeft: Int, toTop: Int): Boolean = false
    override fun animateMove(holder: RecyclerView.ViewHolder?, fromX: Int, fromY: Int, toX: Int, toY: Int): Boolean = false
    override fun runPendingAnimations() {}
    override fun endAnimation(item: RecyclerView.ViewHolder) {}
    override fun endAnimations() {}
    override fun isRunning(): Boolean = false
}
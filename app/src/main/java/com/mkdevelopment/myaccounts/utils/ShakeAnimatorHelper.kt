package com.mkdevelopment.myaccounts.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.LinearInterpolator

class ShakeAnimatorHelper(targetView: View, duration: Int) {
    var isAnimationRunning = false
    private val shakeAnimator: ObjectAnimator = ObjectAnimator.ofFloat(targetView, "translationX", 0f, 25f, -25f, 25f, -25f, 15f, -15f, 6f, -6f, 0f)

    init {
        shakeAnimator.duration = duration.toLong()
        shakeAnimator.interpolator = LinearInterpolator()

        shakeAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                isAnimationRunning = true
            }

            override fun onAnimationCancel(animation: Animator) {
                isAnimationRunning = false
                shakeAnimator.cancel()
            }

            override fun onAnimationEnd(animation: Animator) {
                isAnimationRunning = false
                shakeAnimator.cancel()
            }
        })
    }

    fun startAnimation() {
        if (isAnimationRunning) {
            return
        }

        shakeAnimator.start()
    }
}

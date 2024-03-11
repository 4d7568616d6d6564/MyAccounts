package com.mkdevelopment.myaccounts.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Context.VIBRATOR_MANAGER_SERVICE
import android.content.Context.VIBRATOR_SERVICE
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.View
import android.view.animation.LinearInterpolator

class ShakeAnimatorHelper(val context: Context, targetView: View, duration: Int) {
    var isAnimationRunning = false
    private val shakeAnimator: ObjectAnimator = ObjectAnimator.ofFloat(
        targetView,
        "translationX",
        0f,
        25f,
        -25f,
        25f,
        -25f,
        15f,
        -15f,
        6f,
        -6f,
        0f
    )

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
        vibrate(shakeAnimator.duration)
    }

    @Suppress("DEPRECATION")
    private fun vibrate(milliseconds: Long) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.cancel()
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            vibrator.vibrate(milliseconds)
        }
    }
}

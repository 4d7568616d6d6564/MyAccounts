package com.mkdevelopment.myaccounts.viewmodel

import android.app.Application
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.common.PinIndicatorCount
import com.mkdevelopment.myaccounts.utils.ShakeAnimatorHelper
import com.mkdevelopment.myaccounts.utils.SharedPreferencesHelper
import com.mkdevelopment.myaccounts.utils.VibratorHelper
import com.mkdevelopment.myaccounts.view.IndicatorView
import com.mkdevelopment.myaccounts.view.PinNumpadView


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private var previousPassword: String = ""
    private var confirmPin: Boolean = false
    private var isHandlerExecuted: Boolean = false
    private val handler = Handler(Looper.getMainLooper())
    private var remainingTime: Long = 0
    private var defaultRemainingTimeV1: Long = 30000
    private var defaultRemainingTimeV2: Long = 60000
    private var defaultRemainingTimeV3: Long = 90000
    private var defaultRemainingTimeV4: Long = 120000
    private var defaultRemainingTimeV5: Long = 200000
    private var defaultRemainingTimeV6: Long = 300000
    private lateinit var countdownTimer: CountDownTimer


    interface PinInputCallback {
        fun onPinResult(result: Boolean)
    }

    fun handleCountDown(
        pinNumpadView: PinNumpadView,
        textDesc: TextView,
        textTime: TextView
    ) {
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
        remainingTime = SharedPreferencesHelper(getApplication()).getProtectionRemainingTime()
        pinNumpadView.disableButtons()
        textTime.visibility = View.VISIBLE
        textDesc.text =
            getApplication<Application>().getString(R.string.account_lockout_warning)
        startCountdown(textTime, textDesc, pinNumpadView)
    }

    fun handlePinLogin(
        pinLength: Int,
        pinNumpadView: PinNumpadView,
        indicatorView: IndicatorView,
        textDesc: TextView,
        textTime: TextView,
        callback: PinInputCallback
    ) {
        if (pinLength == PinIndicatorCount.FOUR.count) {
            if (SharedPreferencesHelper(getApplication()).getAccountPassword() == pinNumpadView.getAppendData()) {
                SharedPreferencesHelper(getApplication()).setRetryCount(0)
                callback.onPinResult(true)
            } else {
                if (!isHandlerExecuted) {
                    isHandlerExecuted = true
                    var count = SharedPreferencesHelper(getApplication()).getRetryCount()
                    if (count == 3) {
                        remainingTime = defaultRemainingTimeV1
                        SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                            defaultRemainingTimeV1
                        )
                        handleCountDown(pinNumpadView, textDesc, textTime)
                        ShakeAnimatorHelper(getApplication(), pinNumpadView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                        }, 250)

                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                    } else if (count == 5) {
                        remainingTime = defaultRemainingTimeV2
                        SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                            defaultRemainingTimeV2
                        )
                        handleCountDown(pinNumpadView, textDesc, textTime)
                        ShakeAnimatorHelper(getApplication(), pinNumpadView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                        }, 250)

                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                    } else if (count == 7) {
                        remainingTime = defaultRemainingTimeV3
                        SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                            defaultRemainingTimeV3
                        )
                        handleCountDown(pinNumpadView, textDesc, textTime)
                        ShakeAnimatorHelper(getApplication(), pinNumpadView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                        }, 250)

                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                    } else if (count == 9) {
                        remainingTime = defaultRemainingTimeV4
                        SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                            defaultRemainingTimeV4
                        )
                        handleCountDown(pinNumpadView, textDesc, textTime)
                        ShakeAnimatorHelper(getApplication(), pinNumpadView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                        }, 250)

                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                    } else if (count == 11) {
                        remainingTime = defaultRemainingTimeV5
                        SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                            defaultRemainingTimeV5
                        )
                        handleCountDown(pinNumpadView, textDesc, textTime)
                        ShakeAnimatorHelper(getApplication(), pinNumpadView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                        }, 250)

                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                    } else if (count > 12) {
                        remainingTime = defaultRemainingTimeV6
                        SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                            defaultRemainingTimeV6
                        )
                        handleCountDown(pinNumpadView, textDesc, textTime)
                        ShakeAnimatorHelper(getApplication(), pinNumpadView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                        }, 250)

                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                    } else {
                        SharedPreferencesHelper(getApplication()).setRetryCount(++count)
                        ShakeAnimatorHelper(getApplication(), indicatorView, 500).startAnimation()
                        handler.postDelayed({
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                            isHandlerExecuted = false
                            textDesc.text =
                                getApplication<Application>().getString(R.string.invalid_password)
                        }, 250)
                    }
                }
            }
        }
    }

    fun handlePinCreate(
        pinLength: Int,
        pinNumpadView: PinNumpadView,
        indicatorView: IndicatorView,
        textDesc: TextView,
        callback: PinInputCallback
    ) {
        if (pinLength == PinIndicatorCount.FOUR.count) {
            if (!confirmPin) {
                if (!isHandlerExecuted) {
                    isHandlerExecuted = true
                    previousPassword = pinNumpadView.getAppendData()
                    handler.postDelayed({
                        pinNumpadView.clearPinBuilder()
                        indicatorView.clearIndicators()
                        confirmPin = true
                        isHandlerExecuted = false
                        textDesc.text =
                            getApplication<Application>().getString(R.string.re_enter_password)
                    }, 250)
                }
            } else {
                if (!isHandlerExecuted) {
                    isHandlerExecuted = true
                    if (previousPassword == pinNumpadView.getAppendData()) {
                        SharedPreferencesHelper(getApplication()).setAccountPassword(pinNumpadView.getAppendData())
                        SharedPreferencesHelper(getApplication()).setAccountCreated(true)
                        showToast(getApplication<Application>().getString(R.string.account_created_successfully))
                        callback.onPinResult(true)
                    } else {
                        ShakeAnimatorHelper(getApplication(), indicatorView, 500).startAnimation()
                        textDesc.text =
                            getApplication<Application>().getString(R.string.passwords_do_not_match)
                        previousPassword = ""
                        confirmPin = false
                        handler.postDelayed({
                            isHandlerExecuted = false
                            pinNumpadView.clearPinBuilder()
                            indicatorView.clearIndicators()
                        }, 250)
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun startCountdown(
        textTime: TextView,
        textDesc: TextView,
        pinNumpadView: PinNumpadView
    ) {
        countdownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                textTime.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                pinNumpadView.enableButtons()
                textDesc.text =
                    getApplication<Application>().getString(R.string.enter_account_password)
                textTime.visibility = View.INVISIBLE
                remainingTime = 0
                SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(
                    remainingTime
                )
            }
        }
        countdownTimer.start()
    }

    fun onDestroyActivity() {
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    fun onPauseActivity() {
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
            SharedPreferencesHelper(getApplication()).setProtectionRemainingTime(remainingTime)
        }
    }
}

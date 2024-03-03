package com.mkdevelopment.myaccounts.activity

import android.app.ActivityOptions
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.databinding.ActivityLoginBinding
import com.mkdevelopment.myaccounts.utils.SharedPreferencesHelper
import com.mkdevelopment.myaccounts.view.IndicatorView
import com.mkdevelopment.myaccounts.view.PinNumpadView
import com.mkdevelopment.myaccounts.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var pinNumpadView: PinNumpadView
    private lateinit var indicatorView: IndicatorView
    private lateinit var textDesc: TextView
    private lateinit var textTime: TextView
    private var isAccountCreated: Boolean = false
    private var remainingTime: Long = 0
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pinNumpadView = binding.pinNumpadView
        indicatorView = binding.indicatorView
        textDesc = binding.textDesc
        textTime = binding.textTime

        isAccountCreated = SharedPreferencesHelper(this).isAccountCreated()
        remainingTime = SharedPreferencesHelper(this).getProtectionRemainingTime()

        val screenWidth = Resources.getSystem().displayMetrics.widthPixels
        binding.backgroundImageView.setImageResource(R.drawable.ic_ui_linear_lock)
        binding.backgroundImageView.translationX = -(screenWidth / 3).toFloat()

        textDesc.viewTreeObserver.addOnPreDrawListener(object :
            ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                textDesc.viewTreeObserver.removeOnPreDrawListener(this)
                return true
            }
        })

        pinNumpadView.setOnPinChangedListener(object : PinNumpadView.OnPinChangedListener {
            override fun onPinChanged(pin: String) {
                indicatorView.updateIndicators(pin.length)
            }
        })

        pinNumpadView.setOnPinLengthChangedListener(object :
            PinNumpadView.OnPinLengthChangedListener {
            override fun onPinLengthChanged(pinLength: Int) {
                if (isAccountCreated) {
                    loginViewModel.handlePinLogin(
                        pinLength,
                        pinNumpadView,
                        indicatorView,
                        textDesc,
                        textTime,
                        object : LoginViewModel.PinInputCallback {
                            override fun onPinResult(result: Boolean) {
                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                val options = ActivityOptions.makeCustomAnimation(
                                    this@LoginActivity,
                                    R.anim.from_right,
                                    R.anim.to_left
                                )
                                startActivity(intent, options.toBundle())
                                finish()
                            }
                        })
                } else {
                    loginViewModel.handlePinCreate(
                        pinLength,
                        pinNumpadView,
                        indicatorView,
                        textDesc,
                        object : LoginViewModel.PinInputCallback {
                            override fun onPinResult(result: Boolean) {
                                if (result) {
                                    val intent =
                                        Intent(this@LoginActivity, MainActivity::class.java)
                                    val options = ActivityOptions.makeCustomAnimation(
                                        this@LoginActivity,
                                        R.anim.from_right,
                                        R.anim.to_left
                                    )
                                    startActivity(intent, options.toBundle())
                                    finish()
                                }
                            }
                        })
                }
            }
        })
    }


    override fun onResume() {
        super.onResume()
        if (remainingTime > 0) {
            textDesc.text = getString(R.string.account_lockout_warning)
            loginViewModel.handleCountDown(pinNumpadView, textDesc, textTime)
        } else {
            if (isAccountCreated) {
                textDesc.text = getString(R.string.enter_account_password)
            } else {
                textDesc.text = getString(R.string.password_setup_prompt)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.onDestroyActivity()
    }

    override fun onPause() {
        super.onPause()
        loginViewModel.onPauseActivity()
    }
}
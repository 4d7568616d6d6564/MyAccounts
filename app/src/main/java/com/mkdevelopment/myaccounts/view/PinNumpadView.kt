package com.mkdevelopment.myaccounts.view

import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import com.google.android.material.button.MaterialButton
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.common.PinIndicatorCount

class PinNumpadView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : GridLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private val pinBuilder = StringBuilder()
    private var onPinChangedListener: OnPinChangedListener? = null
    private var onPinLengthChangedListener: OnPinLengthChangedListener? = null
    private val indicatorCount = PinIndicatorCount.FOUR.count
    private val buttons = mutableListOf<MaterialButton>()

    init {
        LayoutInflater.from(context).inflate(R.layout.pin_layout, this, true)
        setOnClickListener(this)

        val buttonIds = intArrayOf(
            R.id.btn1, R.id.btn2, R.id.btn3,
            R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9,
            R.id.btn0, R.id.btnClear
        )

        val screenHeight = Resources.getSystem().displayMetrics.heightPixels

        buttonIds.forEach { setupButton(it, ((screenHeight) / 8)) }
    }

    private fun setupButton(buttonId: Int, buttonSize: Int) {
        val button = findViewById<MaterialButton>(buttonId)
        button.layoutParams.width = buttonSize
        button.layoutParams.height = buttonSize

        button.iconSize = (buttonSize / 3)
        buttons.add(button)

        button.setOnClickListener(this)
    }


    override fun onClick(view: View) {
        if (view is Button) {
            val buttonText = view.text.toString()

            if (view.id == R.id.btnClear) {
                undoPin()
            } else {
                appendToPin(buttonText)
            }

            onPinChangedListener?.onPinChanged(pinBuilder.toString())
            onPinLengthChangedListener?.onPinLengthChanged(pinBuilder.length)
        }
    }

    private fun undoPin() {
        if (pinBuilder.isNotEmpty()) {
            pinBuilder.deleteCharAt(pinBuilder.length - 1)
        }
    }

    fun clearPinBuilder() {
        pinBuilder.clear()
    }

    private fun appendToPin(text: String) {
        if (pinBuilder.length < indicatorCount) {
            pinBuilder.append(text)
        }
    }

    fun setOnPinChangedListener(listener: OnPinChangedListener) {
        onPinChangedListener = listener
    }

    fun setOnPinLengthChangedListener(listener: OnPinLengthChangedListener) {
        onPinLengthChangedListener = listener
    }


    fun getLength(): Int {
        return pinBuilder.length
    }

    fun getAppendData(): String {
        return pinBuilder.toString()
    }

    fun enableButtons() {
        buttons.forEach { it.isEnabled = true }
    }

    fun disableButtons() {
        buttons.forEach { it.isEnabled = false }
    }

    fun buttonSizeChange(buttonSize: Int) {
        buttons.forEach {
            it.layoutParams.width = buttonSize
            it.layoutParams.height = buttonSize
            it.iconSize = (buttonSize / 3)
        }
    }

    interface OnPinChangedListener {
        fun onPinChanged(pin: String)
    }

    interface OnPinLengthChangedListener {
        fun onPinLengthChanged(pinLength: Int)
    }

}

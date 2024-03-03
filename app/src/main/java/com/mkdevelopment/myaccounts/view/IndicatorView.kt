package com.mkdevelopment.myaccounts.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.mkdevelopment.myaccounts.R
import com.mkdevelopment.myaccounts.common.PinIndicatorCount

class IndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val indicatorViews = mutableListOf<View>()

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        val indicatorCount = PinIndicatorCount.FOUR.count

        for (i in 0 until indicatorCount) {
            val indicatorView = View(context)
            val layoutParams = LayoutParams(
                resources.getDimension(R.dimen._15sdp).toInt(),
                resources.getDimension(R.dimen._15sdp).toInt()
            )
            layoutParams.marginEnd =  resources.getDimension(R.dimen._3sdp).toInt()
            layoutParams.marginStart =  resources.getDimension(R.dimen._3sdp).toInt()
            indicatorView.layoutParams = layoutParams
            indicatorView.background =
                ContextCompat.getDrawable(context, R.drawable.indicator_empty)
            addView(indicatorView)
            indicatorViews.add(indicatorView)
        }
    }
    fun updateIndicators(pinLength: Int) {
        for (i in indicatorViews.indices) {
            if (i < pinLength) {
                indicatorViews[i].background =
                    ContextCompat.getDrawable(context, R.drawable.indicator_filled)
            } else {
                indicatorViews[i].background =
                    ContextCompat.getDrawable(context, R.drawable.indicator_empty)
            }
        }
    }

    fun clearIndicators() {
        for (i in indicatorViews.indices) {
            indicatorViews[i].background =
                ContextCompat.getDrawable(context, R.drawable.indicator_empty)
        }
    }
}

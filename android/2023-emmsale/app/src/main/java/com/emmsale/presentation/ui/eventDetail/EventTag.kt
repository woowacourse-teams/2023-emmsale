package com.emmsale.presentation.ui.eventDetail

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.emmsale.R

class EventTag : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initView()
    }

    private fun initView() {
        isClickable = false
        textSize = 12F
        gravity = Gravity.CENTER
        minimumHeight = 0
        background = ContextCompat.getDrawable(context, R.drawable.bg_eventdetail_tag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

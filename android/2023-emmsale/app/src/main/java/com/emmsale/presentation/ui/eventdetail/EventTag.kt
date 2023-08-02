package com.emmsale.presentation.ui.eventdetail

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import com.emmsale.R
import com.emmsale.presentation.utils.extension.px

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
        updatePadding(12.px, 3.px, 12.px, 3.px)
    }
}

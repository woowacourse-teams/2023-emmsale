package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import com.emmsale.R

class EventTag : AppCompatCheckBox {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initView()
    }

    private fun initView() {
        isClickable = false
        buttonDrawable = null
        textSize = 13F
        gravity = Gravity.CENTER
        minimumHeight = 0
        background = ContextCompat.getDrawable(context, R.drawable.bg_event_tag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

fun Context.eventChipOf(
    block: EventTag.() -> Unit,
): EventTag = EventTag(this).apply(block)

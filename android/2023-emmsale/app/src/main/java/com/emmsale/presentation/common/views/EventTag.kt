package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.Dimension
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
        setTextSize(Dimension.SP, 13F)
        gravity = Gravity.CENTER
        minimumHeight = 0
        background = ContextCompat.getDrawable(context, R.drawable.bg_event_tag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

fun Context.eventChipOf(
    block: EventTag.() -> Unit,
): EventTag = EventTag(this).apply(block)

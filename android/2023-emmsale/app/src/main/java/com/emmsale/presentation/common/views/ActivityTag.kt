package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.emmsale.R

class ActivityTag : AppCompatCheckBox {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initView()
    }

    private fun initView() {
        setTextSize(Dimension.SP, 13F)
        isClickable = true
        buttonDrawable = null
        gravity = Gravity.CENTER
        minimumHeight = 0
        background = ContextCompat.getDrawable(context, R.drawable.bg_activity_tag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

fun Fragment.activityChipOf(
    block: ActivityTag.() -> Unit,
): ActivityTag = requireContext().activityChipOf(block)

fun Context.activityChipOf(
    block: ActivityTag.() -> Unit,
): ActivityTag = ActivityTag(this).apply(block)

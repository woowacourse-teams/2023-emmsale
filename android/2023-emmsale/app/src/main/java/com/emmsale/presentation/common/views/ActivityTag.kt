package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.emmsale.R
import com.emmsale.presentation.utils.extension.px

class ActivityTag : AppCompatCheckBox {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initView()
    }

    private fun initView() {
        isClickable = true
        buttonDrawable = null
        textSize = 13F
        gravity = Gravity.CENTER
        background = ContextCompat.getDrawable(context, R.drawable.bg_activity_tag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
        updatePadding(12.px, 0, 12.px, 0)
    }
}

fun Fragment.chipOf(
    block: ActivityTag.() -> Unit
): ActivityTag = requireContext().chipOf(block)

fun Context.chipOf(
    block: ActivityTag.() -> Unit
): ActivityTag = ActivityTag(this).apply(block)

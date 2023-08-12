package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.emmsale.R

class FilterTag : AppCompatButton {
    constructor(context: Context) : super(context, null, R.attr.FilterTagStyle)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        R.attr.FilterTagStyle
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}

fun Context.filterChipOf(
    block: FilterTag.() -> Unit,
): FilterTag = FilterTag(this).apply(block)

fun Fragment.filterChipOf(
    block: FilterTag.() -> Unit,
): FilterTag = requireContext().filterChipOf(block)

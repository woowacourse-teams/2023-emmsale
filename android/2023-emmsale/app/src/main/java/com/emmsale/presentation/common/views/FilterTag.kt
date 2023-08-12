package com.emmsale.presentation.common.views

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.emmsale.R
import com.emmsale.presentation.common.extension.dp

class FilterTag : AppCompatButton {
    constructor(context: Context) : super(context, null, R.attr.FilterTagStyle)
    constructor(context: Context, attrs: AttributeSet?) : super(
        context,
        attrs,
        R.attr.FilterTagStyle,
    )

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr,
    )

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val marginLayoutParams = MarginLayoutParams::class.java.cast(layoutParams)
        marginLayoutParams.marginEnd = 5.dp
        layoutParams = marginLayoutParams
    }
}

fun Context.filterChipOf(
    block: FilterTag.() -> Unit,
): FilterTag = FilterTag(this).apply(block)

fun Fragment.filterChipOf(
    block: FilterTag.() -> Unit,
): FilterTag = requireContext().filterChipOf(block)

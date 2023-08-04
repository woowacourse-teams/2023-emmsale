package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.emmsale.R
import com.emmsale.presentation.utils.extension.dp

class FilterTag : AppCompatTextView {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        initView()
    }

    private fun initView() {
        textSize = 13F
        gravity = Gravity.CENTER
        minimumHeight = 0
        background = ContextCompat.getDrawable(context, R.drawable.bg_filter_tag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMarginEnd(5.dp)
    }

    private fun setMarginEnd(margin: Int) {
        val margins = ViewGroup.MarginLayoutParams::class.java.cast(layoutParams)
        layoutParams = margins?.apply {
            marginEnd = margin
        }
    }
}

fun Context.filterChipOf(
    block: FilterTag.() -> Unit,
): FilterTag = FilterTag(this).apply(block)

fun Fragment.filterChipOf(
    block: FilterTag.() -> Unit,
): FilterTag = requireContext().filterChipOf(block)

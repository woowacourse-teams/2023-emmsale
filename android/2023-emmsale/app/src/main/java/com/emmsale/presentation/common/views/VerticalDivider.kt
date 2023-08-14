package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.emmsale.R

class VerticalDivider(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs) {
    init {
        setBackgroundResource(R.drawable.bg_all_vertical_divider)
    }
}

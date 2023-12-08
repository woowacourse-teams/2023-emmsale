package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.emmsale.R

class HorizontalDivider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatImageView(context, attrs) {
    init {
        setImageResource(R.drawable.bg_all_horizontal_divider)
    }
}

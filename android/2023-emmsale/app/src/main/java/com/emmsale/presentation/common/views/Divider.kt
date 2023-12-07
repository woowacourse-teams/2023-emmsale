package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.emmsale.R

class Divider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {
    init {
        setBackgroundResource(R.drawable.bg_all_divider)
    }
}

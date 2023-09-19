package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import com.emmsale.R

class CategoryTagChip(
    context: Context,
    attrs: AttributeSet? = null,
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
        setTextSize(Dimension.SP, 13F)
        gravity = Gravity.CENTER
        background = ContextCompat.getDrawable(context, R.drawable.bg_profile_categorytag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

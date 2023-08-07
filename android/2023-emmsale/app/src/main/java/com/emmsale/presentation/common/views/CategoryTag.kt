package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import com.emmsale.R

class CategoryTag(
    context: Context,
    attrs: AttributeSet? = null,
) : androidx.appcompat.widget.AppCompatTextView(context, attrs) {

    init {
        setTextSize(Dimension.SP, 13f)
        gravity = Gravity.CENTER
        background = ContextCompat.getDrawable(context, R.drawable.bg_myprofile_categorytag)
        setTextColor(ContextCompat.getColor(context, R.color.black))
    }
}

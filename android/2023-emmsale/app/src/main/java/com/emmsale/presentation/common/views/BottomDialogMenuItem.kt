package com.emmsale.presentation.common.views

import android.content.Context
import android.graphics.Typeface
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatTextView
import com.emmsale.presentation.common.extension.dp

class BottomDialogMenuItem(context: Context) : AppCompatTextView(context) {

    init {
        setTypeface(null, Typeface.BOLD)
        setTextSize(Dimension.SP, 14F)
        setPadding(0, 21.dp, 0, 21.dp)
    }

    companion object {
        const val NORMAL = 0
        const val DANGER = 4444
    }
}

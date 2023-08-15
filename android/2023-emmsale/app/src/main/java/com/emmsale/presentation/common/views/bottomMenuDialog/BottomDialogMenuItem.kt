package com.emmsale.presentation.common.views.bottomMenuDialog

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.emmsale.R
import kotlin.math.roundToInt

class BottomDialogMenuItem(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatTextView(context, attrs, R.style.BottomDialogMenuItemStyle) {

    init {
        val verticalPadding =
            resources.getDimension(R.dimen.padding_bottommenudialog_menu_item_vertical)
                .roundToInt()
        setPadding(0, verticalPadding, 0, verticalPadding)
    }
}

package com.emmsale.presentation.common.views.bottomMenuDialog

import android.content.Context
import android.graphics.Typeface
import androidx.appcompat.widget.AppCompatTextView
import com.emmsale.R
import kotlin.math.roundToInt

class BottomDialogMenuItem(context: Context) : AppCompatTextView(context) {

    init {
        setTypeface(null, Typeface.BOLD)
        textSize = resources.getDimension(R.dimen.textsize_bottomdialogmenuitem_title)
        val verticalPadding =
            resources.getDimension(R.dimen.padding_bottommenudialog_menu_item_vertical)
                .roundToInt()
        setPadding(0, verticalPadding, 0, verticalPadding)
    }
}

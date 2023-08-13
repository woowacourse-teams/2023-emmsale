package com.emmsale.presentation.common.views.bottomMenuDialog

import android.content.Context
import androidx.core.content.ContextCompat
import com.emmsale.R

enum class MenuItemType {
    NORMAL {
        override fun getColor(context: Context): Int =
            ContextCompat.getColor(context, R.color.black)
    },
    IMPORTANT {
        override fun getColor(context: Context): Int =
            ContextCompat.getColor(context, R.color.red)
    }, ;

    abstract fun getColor(context: Context): Int
}

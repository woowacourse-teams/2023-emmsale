package com.emmsale.presentation.common.layoutManager

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CenterSmoothScroller @Inject constructor(
    @ApplicationContext context: Context,
) : LinearSmoothScroller(context) {

    override fun calculateDtToFit(
        viewStart: Int,
        viewEnd: Int,
        boxStart: Int,
        boxEnd: Int,
        snapPreference: Int,
    ): Int = (boxStart + (boxEnd - boxStart) / 2) - (viewStart + (viewEnd - viewStart) / 2)
}

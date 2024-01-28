package com.emmsale.presentation.common.layoutManager

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class EndSmoothScroller @Inject constructor(
    @ApplicationContext context: Context,
) : LinearSmoothScroller(context) {

    override fun getVerticalSnapPreference(): Int = SNAP_TO_END
}

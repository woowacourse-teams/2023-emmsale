package com.emmsale.presentation.ui.main.myProfile.editMyProfile.recyclerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.common.extension.dp

class ActivitiesAdapterDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        val offset = ACTIVITIES_INTERVAL

        if (position != 0) {
            outRect.top = offset
        }
    }

    companion object {
        private val ACTIVITIES_INTERVAL = 10.dp
    }
}

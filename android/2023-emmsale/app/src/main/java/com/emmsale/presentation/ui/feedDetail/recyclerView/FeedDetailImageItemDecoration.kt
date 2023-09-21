package com.emmsale.presentation.ui.feedDetail.recyclerView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.presentation.common.extension.dp

class FeedDetailImageItemDecoration(
    private val divWidth: Int = 10.dp,
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State,
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val position = parent.getChildAdapterPosition(view)
        if (position > 0) outRect.left = divWidth
    }
}

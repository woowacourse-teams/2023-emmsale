package com.emmsale.presentation.common

import android.content.Context
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R

class ScrollUpVisibilityListener(
    private val context: Context,
    private val targetView: View,
) : RecyclerView.OnScrollListener() {

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        val isLandScape = context.resources.getBoolean(R.bool.is_landscape)
        val scrollUpStandard = if (isLandScape) {
            LANDSCAPE_SCROLL_UP_STANDARD_POSITION
        } else {
            PORTRAIT_SCROLL_UP_STANDARD_POSITION
        }

        targetView.isVisible = lastVisibleItemPosition >= scrollUpStandard
    }

    companion object {
        private const val PORTRAIT_SCROLL_UP_STANDARD_POSITION = 3
        private const val LANDSCAPE_SCROLL_UP_STANDARD_POSITION = 6
    }
}

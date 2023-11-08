package com.emmsale.presentation.common

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emmsale.R

class ScrollTopListener(
    private val targetView: View,
) : RecyclerView.OnScrollListener() {
    private val isLandScape = getIsLandscape()
    private val scrollUpStandardPosition = getScrollUpStandardPosition(isLandScape)

    private fun getIsLandscape(): Boolean {
        return targetView.context.resources.getBoolean(R.bool.is_landscape)
    }

    private fun getScrollUpStandardPosition(isLandScape: Boolean): Int = if (isLandScape) {
        LANDSCAPE_SCROLL_UP_STANDARD_POSITION
    } else {
        PORTRAIT_SCROLL_UP_STANDARD_POSITION
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        val layoutManager = recyclerView.layoutManager as GridLayoutManager
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        setupTargetView(recyclerView, lastVisibleItemPosition)
    }

    private fun setupTargetView(recyclerView: RecyclerView, lastVisibleItemPosition: Int) {
        setupTargetViewClickListener(recyclerView)
        targetView.isVisible = isVisibleTargetView(lastVisibleItemPosition)
    }

    private fun setupTargetViewClickListener(recyclerView: RecyclerView) {
        if (targetView.hasOnClickListeners()) return

        targetView.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }
    }

    private fun isVisibleTargetView(lastVisibleItemPosition: Int) =
        lastVisibleItemPosition >= scrollUpStandardPosition

    companion object {
        private const val PORTRAIT_SCROLL_UP_STANDARD_POSITION = 3
        private const val LANDSCAPE_SCROLL_UP_STANDARD_POSITION = 6
    }
}

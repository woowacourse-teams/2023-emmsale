package com.emmsale.presentation.common.bindingadapter

import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("onRefresh")
fun SwipeRefreshLayout.setOnRefresh(onRefresh: () -> Unit) {
    setOnRefreshListener {
        onRefresh()
        isRefreshing = false
    }
}

@BindingAdapter("app:swipeRefreshColor")
fun SwipeRefreshLayout.setSwipeRefreshColor(@ColorInt color: Int) {
    setColorSchemeColors(color)
}

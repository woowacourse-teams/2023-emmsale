package com.emmsale.presentation.common.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

@BindingAdapter("onRefresh")
fun setOnRefresh(swipeRefreshLayout: SwipeRefreshLayout, onRefresh: () -> Unit) {
    swipeRefreshLayout.setOnRefreshListener {
        onRefresh()
        swipeRefreshLayout.isRefreshing = false
    }
}

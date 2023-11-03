package com.emmsale.presentation.common.bindingadapter

import androidx.annotation.ColorInt
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

@BindingAdapter("app:onRefresh")
fun SwipeRefreshLayout.setOnRefresh(onRefresh: () -> Unit) {
    setOnRefreshListener {
        onRefresh()
        isRefreshing = false
    }
}

@BindingAdapter("app:onRefresh1")
fun SwipeRefreshLayout.setOnRefresh1(onRefreshListener: OnRefreshListener) {
    setOnRefreshListener {
        val refreshJob = onRefreshListener.onRefresh()
        if (refreshJob.isCancelled) return@setOnRefreshListener

        MainScope().launch {
            refreshJob.join()
            isRefreshing = false
        }
    }
}

fun interface OnRefreshListener {
    fun onRefresh(): Job
}

@BindingAdapter("app:swipeRefreshColor")
fun SwipeRefreshLayout.setSwipeRefreshColor(@ColorInt color: Int) {
    setColorSchemeColors(color)
}

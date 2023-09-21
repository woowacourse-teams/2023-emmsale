package com.emmsale.presentation.common.bindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter

@BindingAdapter("app:layoutMarginTop")
fun View.setLayoutMarginTop(dimen: Float) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = dimen.toInt()
    }
}

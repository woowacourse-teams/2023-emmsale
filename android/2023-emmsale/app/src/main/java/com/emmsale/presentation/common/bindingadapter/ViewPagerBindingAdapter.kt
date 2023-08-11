package com.emmsale.presentation.common.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

@BindingAdapter("app:isUserInputEnabled")
fun ViewPager2.setUserInput(isInputEnabled: Boolean) {
    isUserInputEnabled = isInputEnabled
}

package com.emmsale.presentation.utils.bindingadapter

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2

@BindingAdapter("app:isUserInputEnabled")
fun ViewPager2.setUserInput(isInputEnabled: Boolean) {
    isUserInputEnabled = isInputEnabled
}

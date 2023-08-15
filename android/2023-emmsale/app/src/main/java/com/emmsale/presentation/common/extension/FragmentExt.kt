package com.emmsale.presentation.common.extension

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

fun Fragment.showToast(text: String) {
    requireContext().showToast(text)
}

fun Fragment.showToast(@StringRes textResId: Int) {
    showToast(getString(textResId))
}

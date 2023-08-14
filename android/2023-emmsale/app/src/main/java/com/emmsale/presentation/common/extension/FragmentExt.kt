package com.emmsale.presentation.common.extension

import androidx.fragment.app.Fragment

fun Fragment.showToast(text: String) {
    requireContext().showToast(text)
}

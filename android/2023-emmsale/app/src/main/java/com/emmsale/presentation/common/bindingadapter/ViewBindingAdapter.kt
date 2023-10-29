package com.emmsale.presentation.common.bindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.emmsale.presentation.common.extension.dp

private const val DIMEN_UNIT_DP = "dp"

@BindingAdapter("app:visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:layoutMarginTop")
fun View.setLayoutMarginTop(dimen: Float) {
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = dimen.toInt()
    }
}

@BindingAdapter("app:layout_marginTop")
fun View.setLayoutMarginTop(margin: String) {
    validateMarginFormat(margin)
    updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = margin.toDimen()
    }
}

private fun validateMarginFormat(margin: String) {
    require(margin.endsWith(DIMEN_UNIT_DP) || margin.all { it.isDigit() }) {
        "숫자만 이루어져 있거나 숫자 뒤에 $DIMEN_UNIT_DP 문자열만 올 수 있습니다."
    }
}

private fun String.toDimen(): Int = if (endsWith(DIMEN_UNIT_DP)) {
    dropLastWhile { !it.isDigit() }
        .toInt().dp
} else {
    toInt()
}

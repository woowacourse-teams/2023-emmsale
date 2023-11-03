package com.emmsale.presentation.common.bindingadapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.databinding.BindingAdapter
import com.emmsale.presentation.common.extension.dp

private const val DP_UNIT = "dp"

@BindingAdapter("app:visible")
fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

@BindingAdapter("app:layout_marginTop")
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
    require(margin.endsWith(DP_UNIT) || margin.all { it.isDigit() }) {
        "숫자만 이루어져 있거나 숫자 뒤에 $DP_UNIT 문자열만 올 수 있습니다."
    }
}

private fun String.toDimen(): Int = if (endsWith(DP_UNIT)) {
    removeSuffix(DP_UNIT).toInt().dp
} else {
    toInt()
}

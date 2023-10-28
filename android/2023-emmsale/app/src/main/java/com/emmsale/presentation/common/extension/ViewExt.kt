package com.emmsale.presentation.common.extension

import android.util.TypedValue
import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay

private const val BACKGROUND_SETTING_DELAY: Long = 300

fun View.showSnackBar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.showSnackBar(@StringRes messageResId: Int) {
    Snackbar.make(this, context.getString(messageResId), Snackbar.LENGTH_SHORT).show()
}

suspend fun View.highlight() {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
    setBackgroundResource(typedValue.resourceId)
    delay(BACKGROUND_SETTING_DELAY)
    isPressed = true
}

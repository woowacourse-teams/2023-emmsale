package com.emmsale.presentation.common.extension

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_SHORT).show()
}

fun View.showSnackbar(@StringRes messageResId: Int) {
    Snackbar.make(this, context.getString(messageResId), Snackbar.LENGTH_SHORT).show()
}

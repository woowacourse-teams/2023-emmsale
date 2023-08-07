package com.emmsale.presentation.common.extension

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.emmsale.R

fun Context.showDialog(block: AlertDialog.Builder.() -> Unit) {
    AlertDialog.Builder(this).apply(block).create().show()
}

fun AlertDialog.Builder.title(text: String): AlertDialog.Builder = this.setTitle(text)

fun AlertDialog.Builder.message(text: String): AlertDialog.Builder = this.setMessage(text)

fun AlertDialog.Builder.positiveButton(
    text: String = context.getString(R.string.all_okay),
    handleClick: (which: Int) -> Unit = {},
): AlertDialog.Builder {
    return this.setPositiveButton(text) { _, which -> handleClick(which) }
}

fun AlertDialog.Builder.negativeButton(
    text: String = context.getString(R.string.all_cancel),
    handleClick: (which: Int) -> Unit = {},
): AlertDialog.Builder {
    return this.setNegativeButton(text) { _, which -> handleClick(which) }
}

package com.emmsale.presentation.common.extension

import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

private const val DELAY_SHOW_SOFT_INPUT: Long = 200

fun EditText.showKeyboard() {
    val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
    postDelayed(
        {
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            setSelection(text.length)
        },
        DELAY_SHOW_SOFT_INPUT,
    )
}

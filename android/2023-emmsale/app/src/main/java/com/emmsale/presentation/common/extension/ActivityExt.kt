package com.emmsale.presentation.common.extension

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

private const val KEYBOARD_SHOW_DELAY: Long = 100

fun Activity.hideKeyboard() {
    val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

fun Activity.showKeyboard() {
    val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.postDelayed({
        imm.showSoftInput(currentFocus, InputMethodManager.SHOW_IMPLICIT)
        if (currentFocus is EditText) {
            val editText = currentFocus as EditText
            editText.setSelection(editText.text.length)
        }
    }, KEYBOARD_SHOW_DELAY)
}

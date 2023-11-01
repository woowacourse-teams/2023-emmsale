package com.emmsale.presentation.common.extension

import android.app.Activity
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity

fun Activity.hideKeyboard() {
    val imm = getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
}

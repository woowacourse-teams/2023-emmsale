package com.emmsale.presentation.ui.messageList

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlin.math.abs

class KeyboardHider(
    private val targetView: View,
    private val sensitivity: Float = DEFAULT_SENSITIVITY,
    private val willConsumeTouchEvent: Boolean = WILL_TOUCH_EVENT_DELIVER,
) {
    private val imm by lazy {
        targetView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private var startY: Float = -1F
    private var movedY: Float = 0F

    fun handleHideness(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startY = event.y
            MotionEvent.ACTION_MOVE -> movedY = abs(event.y - startY)
            MotionEvent.ACTION_UP -> if (canHideKeyboard()) hideKeyboard()
        }
        return willConsumeTouchEvent
    }

    private fun canHideKeyboard(): Boolean {
        return movedY < sensitivity
    }

    private fun hideKeyboard() {
        if (targetView.onCheckIsTextEditor()) {
            imm.hideSoftInputFromWindow(targetView.windowToken, 0)
        }
    }

    companion object {
        private const val DEFAULT_SENSITIVITY: Float = 10F
        private const val WILL_TOUCH_EVENT_DELIVER = false
    }
}

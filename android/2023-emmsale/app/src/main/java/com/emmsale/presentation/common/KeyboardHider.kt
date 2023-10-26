package com.emmsale.presentation.common

import android.app.Activity
import android.view.MotionEvent
import com.emmsale.presentation.common.extension.hideKeyboard
import kotlin.math.abs

class KeyboardHider(
    private val activity: Activity,
    private val sensitivity: Float = DEFAULT_SENSITIVITY,
    private val willConsumeTouchEvent: Boolean = CONSUMED_TOUCH_EVENT,
) {

    private var startY: Float = -1F
    private var movedY: Float = 0F

    fun handleHideness(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startY = event.y
            MotionEvent.ACTION_MOVE -> movedY = abs(event.y - startY)
            MotionEvent.ACTION_UP -> if (canHideKeyboard()) activity.hideKeyboard()
        }
        return willConsumeTouchEvent
    }

    private fun canHideKeyboard(): Boolean {
        return movedY < sensitivity
    }

    companion object {
        private const val DEFAULT_SENSITIVITY: Float = 10F
        private const val CONSUMED_TOUCH_EVENT = false
    }
}

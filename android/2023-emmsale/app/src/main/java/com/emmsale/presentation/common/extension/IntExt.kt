package com.emmsale.presentation.common.extension

import android.content.res.Resources
import kotlin.math.roundToInt

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).roundToInt()

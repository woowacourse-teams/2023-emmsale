package com.emmsale.presentation.utils.binding

import android.app.Activity
import androidx.databinding.ViewDataBinding

fun <T : ViewDataBinding> T.setContentView(activity: Activity): T = run {
    activity.setContentView(root)
    this
}

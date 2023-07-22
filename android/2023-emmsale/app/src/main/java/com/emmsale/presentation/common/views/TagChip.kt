package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip

class TagChip : Chip {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        isCloseIconVisible = false
        isCheckable = true
    }
}

fun Fragment.chipOf(
    block: TagChip.() -> Unit
): TagChip = TagChip(requireContext()).apply(block)

fun Context.chipOf(
    block: TagChip.() -> Unit
): TagChip = TagChip(this).apply(block)

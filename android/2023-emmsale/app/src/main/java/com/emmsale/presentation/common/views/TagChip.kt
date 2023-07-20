package com.emmsale.presentation.common.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.chip.Chip

class TagChip : Chip {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        isCloseIconVisible = true
    }
}

fun chipOf(
    context: Context,
    block: TagChip.() -> Unit
): TagChip = TagChip(context).apply(block)

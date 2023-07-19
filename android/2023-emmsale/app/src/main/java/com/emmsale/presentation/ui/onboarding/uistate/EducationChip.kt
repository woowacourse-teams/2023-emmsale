package com.emmsale.presentation.ui.onboarding.uistate

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.chip.Chip

class EducationChip : Chip {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        isCloseIconVisible = true
    }
}

fun educationChipOf(
    context: Context,
    block: EducationChip.() -> Unit
): EducationChip = EducationChip(context).apply(block)

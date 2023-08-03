package com.emmsale.presentation.utils.extension

import android.widget.Checkable
import androidx.core.view.forEach
import com.google.android.material.chip.ChipGroup

fun ChipGroup.checkAll() {
    forEach { tagView ->
        if (tagView is Checkable) {
            tagView.isChecked = true
        }
    }
}

fun ChipGroup.uncheckAll() {
    forEach { tagView ->
        if (tagView is Checkable) {
            tagView.isChecked = false
        }
    }
}

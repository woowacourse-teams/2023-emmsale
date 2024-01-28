package com.emmsale.presentation.common.bindingadapter

import androidx.databinding.BindingAdapter
import com.emmsale.data.model.EventTag
import com.emmsale.presentation.common.views.EventTagChip
import com.google.android.material.chip.ChipGroup

@BindingAdapter("app:eventChips")
fun ChipGroup.setEventChips(tags: List<EventTag>?) {
    removeAllViews()
    addEventTags(tags ?: emptyList())
}

private fun ChipGroup.addEventTags(tags: List<EventTag>) {
    tags.forEach { addView(createEventTag(it)) }
}

private fun ChipGroup.createEventTag(tag: EventTag) = EventTagChip(this.context).apply {
    text = tag.name
}

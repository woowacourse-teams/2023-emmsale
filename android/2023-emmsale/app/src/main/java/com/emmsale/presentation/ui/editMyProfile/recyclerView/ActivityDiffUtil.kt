package com.emmsale.presentation.ui.editMyProfile.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.model.Activity

class ActivityDiffUtil : DiffUtil.ItemCallback<Activity>() {
    override fun areItemsTheSame(
        oldItem: Activity,
        newItem: Activity,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: Activity,
        newItem: Activity,
    ): Boolean = oldItem == newItem
}

package com.emmsale.presentation.ui.blockMemberList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.model.BlockedMember

object BlockedMemberDiffUtil : DiffUtil.ItemCallback<BlockedMember>() {
    override fun areItemsTheSame(
        oldItem: BlockedMember,
        newItem: BlockedMember,
    ): Boolean = oldItem.blockId == newItem.blockId

    override fun areContentsTheSame(
        oldItem: BlockedMember,
        newItem: BlockedMember,
    ): Boolean = oldItem == newItem
}

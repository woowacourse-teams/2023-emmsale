package com.emmsale.presentation.ui.setting.block.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.setting.block.uistate.BlockedMemberUiState

object BlockedMemberDiffUtil : DiffUtil.ItemCallback<BlockedMemberUiState>() {
    override fun areItemsTheSame(
        oldItem: BlockedMemberUiState,
        newItem: BlockedMemberUiState,
    ): Boolean = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: BlockedMemberUiState,
        newItem: BlockedMemberUiState,
    ): Boolean = oldItem == newItem
}

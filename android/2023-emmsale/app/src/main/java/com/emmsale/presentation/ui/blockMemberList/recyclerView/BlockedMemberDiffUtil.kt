package com.emmsale.presentation.ui.blockMemberList.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.emmsale.presentation.ui.blockMemberList.uiState.BlockedMemberUiState

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

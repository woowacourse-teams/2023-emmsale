package com.emmsale.presentation.ui.setting.block.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.emmsale.presentation.ui.setting.block.uistate.BlockedMemberUiState

class BlockedMemberAdapter(
    private val onUnblockMemberClick: (memberId: Long) -> Unit,
) : ListAdapter<BlockedMemberUiState, BlockedMemberViewHolder>(BlockedMemberDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedMemberViewHolder =
        BlockedMemberViewHolder(parent, onUnblockMemberClick)

    override fun onBindViewHolder(holder: BlockedMemberViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
package com.emmsale.presentation.ui.setting.block.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemBlockMemberBinding
import com.emmsale.presentation.ui.setting.block.uistate.BlockedMemberUiState

class BlockedMemberViewHolder(
    parent: ViewGroup,
    onUnblockMemberClick: (memberId: Long) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_block_member, parent, false)
) {
    private val binding: ItemBlockMemberBinding by lazy { ItemBlockMemberBinding.bind(itemView) }

    init {
        binding.onUnblockMemberClick = onUnblockMemberClick
    }

    fun bind(blockedMember: BlockedMemberUiState) {
        binding.blockedMember = blockedMember
    }
}

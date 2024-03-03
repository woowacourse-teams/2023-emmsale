package com.emmsale.presentation.ui.blockMemberList.recyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.emmsale.R
import com.emmsale.databinding.ItemBlockMemberBinding
import com.emmsale.model.BlockedMember

class BlockedMemberViewHolder(
    parent: ViewGroup,
    onUnblockMemberClick: (memberId: Long) -> Unit,
) : ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.item_block_member, parent, false),
) {
    private val binding: ItemBlockMemberBinding by lazy { ItemBlockMemberBinding.bind(itemView) }

    init {
        binding.onUnblockMemberClick = onUnblockMemberClick
    }

    fun bind(blockedMember: BlockedMember) {
        binding.blockedMember = blockedMember
    }
}

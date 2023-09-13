package com.emmsale.presentation.ui.main.setting.block.uistate

import com.emmsale.data.model.BlockedMember

data class BlockedMemberUiState(
    val id: Long,
    val blockId: Long,
    val name: String,
    val profileImageUrl: String,
) {
    companion object {
        fun from(blockedMember: BlockedMember): BlockedMemberUiState = BlockedMemberUiState(
            id = blockedMember.id,
            blockId = blockedMember.blockId,
            name = blockedMember.memberName,
            profileImageUrl = blockedMember.profileImageUrl,
        )
    }
}

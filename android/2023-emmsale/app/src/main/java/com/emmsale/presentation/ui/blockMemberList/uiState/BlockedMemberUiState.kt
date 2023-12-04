package com.emmsale.presentation.ui.blockMemberList.uiState

import com.emmsale.data.model.BlockedMember

data class BlockedMemberUiState(
    val id: Long,
    val blockId: Long,
    val name: String,
    val profileImageUrl: String,
) {
    companion object {
        fun from(blockedMember: BlockedMember): BlockedMemberUiState = BlockedMemberUiState(
            id = blockedMember.blockedMemberId,
            blockId = blockedMember.blockId,
            name = blockedMember.memberName,
            profileImageUrl = blockedMember.profileImageUrl,
        )
    }
}

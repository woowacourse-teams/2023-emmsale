package com.emmsale.presentation.ui.profile.uiState

import com.emmsale.data.model.BlockedMember

data class BlockedMemberUiState(
    val blockedMemberId: Long,
    val blockId: Long,
) {
    companion object {
        fun from(blockedMember: BlockedMember) = BlockedMemberUiState(
            blockedMemberId = blockedMember.id,
            blockId = blockedMember.blockId,
        )
    }
}

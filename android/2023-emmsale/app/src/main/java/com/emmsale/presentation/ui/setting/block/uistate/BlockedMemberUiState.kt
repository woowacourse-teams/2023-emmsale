package com.emmsale.presentation.ui.setting.block.uistate

import com.emmsale.data.blockedMember.BlockedMember

data class BlockedMemberUiState(
    val id: Long,
    val name: String,
    val profileImageUrl: String,
) {
    companion object {
        fun from(blockedMember: BlockedMember): BlockedMemberUiState = BlockedMemberUiState(
            id = blockedMember.id,
            name = blockedMember.memberName,
            profileImageUrl = blockedMember.profileImageUrl,
        )
    }
}
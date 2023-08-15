package com.emmsale.presentation.ui.setting.block.uistate

import com.emmsale.data.blockedMember.BlockedMember

data class BlockedMembersUiState(
    val blockedMembers: List<BlockedMemberUiState> = emptyList(),
    val isLoading: Boolean = false,
    val isFetchingError: Boolean = false,
    val isDeletingBlockedMemberError: Boolean = false,
) {
    fun deleteBlockedMember(blockId: Long): BlockedMembersUiState =
        copy(blockedMembers = blockedMembers.filterNot { it.blockId == blockId })

    companion object {
        fun from(blockedMembers: List<BlockedMember>): BlockedMembersUiState =
            BlockedMembersUiState(blockedMembers = blockedMembers.map(BlockedMemberUiState::from))
    }
}

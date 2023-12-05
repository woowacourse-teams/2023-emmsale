package com.emmsale.presentation.ui.blockMemberList.uiState

sealed interface BlockedMembersUiEvent {
    object FetchFail : BlockedMembersUiEvent
    object DeleteFail : BlockedMembersUiEvent
}

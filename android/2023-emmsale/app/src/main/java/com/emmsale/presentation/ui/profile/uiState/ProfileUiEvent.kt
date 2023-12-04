package com.emmsale.presentation.ui.profile.uiState

sealed interface ProfileUiEvent {
    object BlockFail : ProfileUiEvent
    object BlockComplete : ProfileUiEvent
    object UnblockFail : ProfileUiEvent
    object UnblockSuccess : ProfileUiEvent
    object MessageSendFail : ProfileUiEvent
    data class MessageSendComplete(val roomId: String, val otherId: Long) : ProfileUiEvent
    object None : ProfileUiEvent
}

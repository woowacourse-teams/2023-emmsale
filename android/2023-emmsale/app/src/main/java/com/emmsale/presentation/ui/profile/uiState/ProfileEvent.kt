package com.emmsale.presentation.ui.profile.uiState

sealed interface ProfileUiEvent {
    data class UnexpectedError(val errorMessage: String) : ProfileUiEvent
    object BlockFail : ProfileUiEvent
    object BlockComplete : ProfileUiEvent
    object UnblockFail : ProfileUiEvent
    object UnblockSuccess : ProfileUiEvent
    object MessageSendFail : ProfileUiEvent
    data class MessageSendComplete(val roomId: String, val otherId: Long) : ProfileUiEvent
    object None : ProfileUiEvent
}

package com.emmsale.presentation.ui.messageList.uistate

sealed interface MessageListUiEvent {
    object MessageSendComplete : MessageListUiEvent
    object MessageSendFail : MessageListUiEvent
}

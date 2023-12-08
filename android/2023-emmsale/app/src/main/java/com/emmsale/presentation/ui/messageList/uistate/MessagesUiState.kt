package com.emmsale.presentation.ui.messageList.uistate

data class MessagesUiState(
    val messages: List<MessageUiState> = emptyList(),
) {

    val messageSize: Int = messages.size
}

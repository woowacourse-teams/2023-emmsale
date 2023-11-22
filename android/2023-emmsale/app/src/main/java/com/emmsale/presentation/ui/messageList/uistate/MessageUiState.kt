package com.emmsale.presentation.ui.messageList.uistate

import java.time.LocalDateTime

sealed class MessageUiState(
    val content: String,
    val createdAt: LocalDateTime,
) {
    abstract val messageType: MessageType

    enum class MessageType {
        MY,
        OTHER,
        DATE,
    }
}

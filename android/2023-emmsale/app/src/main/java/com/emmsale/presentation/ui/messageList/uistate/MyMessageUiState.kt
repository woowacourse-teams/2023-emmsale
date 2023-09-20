package com.emmsale.presentation.ui.messageList.uistate

import java.time.LocalDateTime

class MyMessageUiState(
    override val messageType: MessageType = MessageType.MY,
    message: String,
    createdAt: LocalDateTime,
    val isFirst: Boolean = true,
) : MessageUiState(message, createdAt)

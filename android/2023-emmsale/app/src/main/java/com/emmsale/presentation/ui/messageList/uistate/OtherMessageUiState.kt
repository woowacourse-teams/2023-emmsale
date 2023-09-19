package com.emmsale.presentation.ui.messageList.uistate

import java.time.LocalDateTime

class OtherMessageUiState(
    override val messageType: MessageType = MessageType.OTHER,
    message: String,
    createdAt: LocalDateTime,
    val profileImageUrl: String,
) : MessageUiState(message, createdAt)

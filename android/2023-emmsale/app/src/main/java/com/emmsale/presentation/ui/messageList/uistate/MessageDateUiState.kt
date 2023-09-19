package com.emmsale.presentation.ui.messageList.uistate

import java.time.LocalDateTime

class MessageDateUiState(
    override val messageType: MessageType = MessageType.DATE,
    date: String,
    createdAt: LocalDateTime,
) : MessageUiState(date, createdAt)

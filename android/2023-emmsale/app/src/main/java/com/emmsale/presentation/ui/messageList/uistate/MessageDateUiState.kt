package com.emmsale.presentation.ui.messageList.uistate

import java.time.LocalDateTime

class MessageDateUiState(
    override val messageType: MessageType = MessageType.DATE,
    messageDate: LocalDateTime,
) : MessageUiState(messageDate.toString(), messageDate)

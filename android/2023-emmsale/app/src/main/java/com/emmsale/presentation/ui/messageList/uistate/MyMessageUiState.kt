package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.data.model.Message
import java.time.LocalDateTime

class MyMessageUiState(
    override val messageType: MessageType = MessageType.MY,
    message: String,
    createdAt: LocalDateTime,
    val isFirst: Boolean = true,
) : MessageUiState(message, createdAt) {

    companion object {
        fun create(
            message: Message,
            isFirst: Boolean = true,
        ): MyMessageUiState = MyMessageUiState(
            message = message.message,
            createdAt = message.createdAt,
            isFirst = isFirst,
        )
    }
}

package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.model.Message

class MyMessageUiState(
    override val messageType: MessageType = MessageType.MY,
    message: Message,
    val isFirst: Boolean = true,
) : MessageUiState(message.content, message.createdAt) {

    companion object {
        fun create(
            message: Message,
            isFirst: Boolean = true,
        ): MyMessageUiState = MyMessageUiState(
            message = message,
            isFirst = isFirst,
        )
    }
}

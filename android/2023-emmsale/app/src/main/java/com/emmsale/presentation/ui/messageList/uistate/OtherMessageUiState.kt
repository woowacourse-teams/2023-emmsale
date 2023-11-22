package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.data.model.Message

class OtherMessageUiState(
    val message: Message,
    val isShownProfile: Boolean = true,
) : MessageUiState(message.content, message.createdAt) {

    override val messageType: MessageType = MessageType.OTHER

    companion object {
        fun create(
            message: Message,
            isShownProfile: Boolean,
        ): OtherMessageUiState = OtherMessageUiState(
            message = message,
            isShownProfile = isShownProfile,
        )
    }
}

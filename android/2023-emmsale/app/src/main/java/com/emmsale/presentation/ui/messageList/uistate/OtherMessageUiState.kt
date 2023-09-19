package com.emmsale.presentation.ui.messageList.uistate

import java.time.LocalDateTime

class OtherMessageUiState(
    override val messageType: MessageType = MessageType.OTHER,
    message: String,
    createdAt: LocalDateTime,
    val memberName: String,
    val profileImageUrl: String,
    val isShownProfile: Boolean = true,
) : MessageUiState(message, createdAt)

package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.data.message.Message
import com.emmsale.data.model.Member
import java.time.LocalDateTime

class OtherMessageUiState(
    override val messageType: MessageType = MessageType.OTHER,
    message: String,
    createdAt: LocalDateTime,
    val senderId: Long,
    val memberName: String,
    val profileImageUrl: String,
    val isShownProfile: Boolean = true,
) : MessageUiState(message, createdAt) {

    companion object {
        fun create(
            message: Message,
            member: Member,
            isShownProfile: Boolean,
        ): OtherMessageUiState = OtherMessageUiState(
            message = message.message,
            createdAt = message.createdAt,
            senderId = message.senderId,
            memberName = member.name,
            profileImageUrl = member.profileImageUrl,
            isShownProfile = isShownProfile,
        )
    }
}

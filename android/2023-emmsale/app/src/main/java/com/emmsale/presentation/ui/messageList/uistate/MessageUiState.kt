package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.data.message.Message
import java.time.LocalDateTime

data class MessageUiState(
    val memberType: MemberType,
    val message: String,
    val createdAt: LocalDateTime,
    val profileImageUrl: String,
) {
    enum class MemberType {
        ME,
        OTHER,
    }

    companion object {
        fun from(
            myUid: Long,
            message: Message,
            profileImageUrl: String?,
        ): MessageUiState = MessageUiState(
            memberType = message.toMessageType(myUid),
            message = message.message,
            createdAt = message.createdAt,
            profileImageUrl = profileImageUrl ?: "",
        )

        private fun Message.toMessageType(myUid: Long): MemberType = when (senderId) {
            myUid -> MemberType.ME
            else -> MemberType.OTHER
        }
    }
}

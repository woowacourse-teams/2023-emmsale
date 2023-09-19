package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.data.message.Message
import java.time.LocalDateTime

abstract class MessageUiState(
    val message: String,
    val createdAt: LocalDateTime,
) {
    abstract val messageType: MessageType

    enum class MessageType {
        MY,
        OTHER,
        DATE,
    }

    override fun equals(other: Any?): Boolean {
        if (other !is MessageUiState) return false
        return message == other.message && createdAt == other.createdAt
    }

    override fun hashCode(): Int {
        var result = message.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + messageType.hashCode()
        return result
    }

    companion object {
        fun from(
            myUid: Long,
            message: Message,
            profileImageUrl: String?,
        ): MessageUiState = when (message.toMessageType(myUid)) {
            MessageType.MY -> MyMessageUiState(
                message = message.message,
                createdAt = message.createdAt,
            )

            MessageType.OTHER -> OtherMessageUiState(
                message = message.message,
                createdAt = message.createdAt,
                profileImageUrl = profileImageUrl ?: "",
            )

            else -> throw IllegalArgumentException("임시 에러")
        }

        private fun Message.toMessageType(myUid: Long): MessageType = when (senderId) {
            myUid -> MessageType.MY
            else -> MessageType.OTHER
        }
    }
}

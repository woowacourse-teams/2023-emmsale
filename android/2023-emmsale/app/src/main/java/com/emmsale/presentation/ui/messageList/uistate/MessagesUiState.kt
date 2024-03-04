package com.emmsale.presentation.ui.messageList.uistate

import com.emmsale.model.Message

data class MessagesUiState(
    val messages: List<MessageUiState> = emptyList(),
) {

    val size: Int = messages.size

    fun isEmpty(): Boolean = messages.isEmpty()

    companion object {
        fun create(messages: List<Message>, myUid: Long): MessagesUiState {
            val messagesNotBlank = messages
                .filter { it.content.isNotBlank() }
                .toUiState(myUid)
            return MessagesUiState(messagesNotBlank)
        }

        private fun List<Message>.toUiState(myUid: Long): List<MessageUiState> {
            val newMessages = mutableListOf<MessageUiState>()

            forEachIndexed { index, message ->
                when {
                    index == 0 -> {
                        newMessages += message.createMessageDateUiState()
                        newMessages += message.createChatMessageUiState(true, myUid)
                    }

                    message.isDifferentDate(this[index - 1]) -> {
                        newMessages += message.createMessageDateUiState()
                    }
                }

                val previousMessage = getOrNull(index - 1) ?: return@forEachIndexed
                val shouldShowProfile = message.shouldShowMemberProfile(previousMessage)
                newMessages += message.createChatMessageUiState(shouldShowProfile, myUid)
            }

            return newMessages
        }

        private fun Message.shouldShowMemberProfile(prevMessage: Message): Boolean {
            return isSameDateTime(prevMessage) ||
                isDifferentSender(prevMessage) ||
                isDifferentDate(prevMessage)
        }

        private fun Message.createMessageDateUiState(): MessageDateUiState = MessageDateUiState(
            messageDate = createdAt,
        )

        private fun Message.createChatMessageUiState(
            shouldShowProfile: Boolean = true,
            myUid: Long,
        ): MessageUiState = when (sender.id) {
            myUid -> MyMessageUiState.create(this, shouldShowProfile)
            else -> OtherMessageUiState.create(this, shouldShowProfile)
        }
    }
}

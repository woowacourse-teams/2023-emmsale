package com.emmsale.data.messageRoom

import com.emmsale.data.message.Message

data class MessageRoom(
    val roomId: Long,
    val senderName: String,
    val messageRoomImageUrl: String,
    val recentMessage: Message,
)

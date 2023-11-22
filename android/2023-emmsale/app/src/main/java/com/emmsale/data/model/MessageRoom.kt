package com.emmsale.data.model

data class MessageRoom(
    val roomId: String,
    val senderName: String,
    val messageRoomImageUrl: String,
    val recentMessage: Message,
)

data class MessageRoom2(
    val roomId: String,
    val interlocutor: Member,
    val recentMessage: Message2,
)

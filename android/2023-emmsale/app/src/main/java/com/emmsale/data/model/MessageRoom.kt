package com.emmsale.data.model

data class MessageRoom(
    val roomId: String,
    val interlocutor: Member,
    val recentMessage: Message,
)

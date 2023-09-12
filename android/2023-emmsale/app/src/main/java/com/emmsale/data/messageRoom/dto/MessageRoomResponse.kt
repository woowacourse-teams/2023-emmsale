package com.emmsale.data.messageRoom.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRoomResponse(
    @SerialName("roomId")
    val roomId: Long,
    @SerialName("interlocutorId")
    val senderId: Long,
    @SerialName("interlocutorName")
    val senderName: String,
    @SerialName("interlocutorAvatar")
    val recentMessage: String,
    @SerialName("recentlyMessage")
    val recentMessageTime: String,
)

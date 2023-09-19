package com.emmsale.data.messageRoom.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRoomResponse(
    @SerialName("roomId")
    val roomId: Long,
    @SerialName("interlocutorId")
    val senderId: Long,
    @SerialName("interlocutorProfile")
    val senderProfileImageUrl: String,
    @SerialName("interlocutorName")
    val senderName: String,
    @SerialName("interlocutorAvatar")
    val recentMessage: String,
    @SerialName("recentlyMessage")
    val recentMessageTime: String,
)

@Serializable
data class MessageResponse(
    @SerialName("senderId")
    val senderId: Long,
    @SerialName("content")
    val message: String,
    @SerialName("createdAt")
    val createdAt: String,
)

@Serializable
data class MessageRequest(
    @SerialName("senderId")
    val senderId: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("content")
    val message: String,
)

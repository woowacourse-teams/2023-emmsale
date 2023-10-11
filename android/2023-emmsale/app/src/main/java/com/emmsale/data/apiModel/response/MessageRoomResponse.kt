package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRoomResponse(
    @SerialName("roomId")
    val roomId: String,
    @SerialName("interlocutorId")
    val senderId: Long,
    @SerialName("interlocutorName")
    val senderName: String,
    @SerialName("interlocutorProfile")
    val senderProfileImageUrl: String,
    @SerialName("recentlyMessage")
    val recentMessage: String,
    @SerialName("recentlyMessageTime")
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
data class MessageSendResponse(
    @SerialName("roomId")
    val roomId: String,
)
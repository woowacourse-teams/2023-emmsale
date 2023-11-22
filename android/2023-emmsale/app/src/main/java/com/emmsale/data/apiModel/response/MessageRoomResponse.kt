package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRoomResponse(
    @SerialName("roomId")
    val roomId: String,
    @SerialName("interlocutor")
    val interlocutor: MemberResponse,
    @SerialName("recentlyMessage")
    val recentMessage: MessageResponse2,
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

@Serializable
data class MessageResponse2(
    @SerialName("id")
    val id: Long,
    @SerialName("sender")
    val sender: MemberResponse,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
)

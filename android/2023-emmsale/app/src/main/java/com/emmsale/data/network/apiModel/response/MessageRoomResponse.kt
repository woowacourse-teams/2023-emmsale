package com.emmsale.data.network.apiModel.response

import com.emmsale.data.network.apiModel.serializer.DateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class MessageRoomResponse(
    @SerialName("roomId")
    val roomId: String,
    @SerialName("interlocutor")
    val interlocutor: MemberResponse,
    @SerialName("recentlyMessage")
    val recentMessage: MessageResponse,
)

@Serializable
data class MessageSendResponse(
    @SerialName("roomId")
    val roomId: String,
)

@Serializable
data class MessageResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("sender")
    val sender: MemberResponse,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,
)

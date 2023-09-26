package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MessageRoomResponse
import com.emmsale.data.model.Message
import com.emmsale.data.model.MessageRoom

fun List<MessageRoomResponse>.toData(): List<MessageRoom> = map { it.toData() }

fun MessageRoomResponse.toData(): MessageRoom = MessageRoom(
    roomId = roomId,
    senderName = senderName,
    messageRoomImageUrl = senderProfileImageUrl,
    recentMessage = Message.create(
        senderId = senderId,
        message = recentMessage,
        createdAt = recentMessageTime,
    ),
)

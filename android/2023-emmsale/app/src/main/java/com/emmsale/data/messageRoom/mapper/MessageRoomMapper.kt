package com.emmsale.data.messageRoom.mapper

import com.emmsale.data.message.Message
import com.emmsale.data.messageRoom.MessageRoom
import com.emmsale.data.messageRoom.dto.MessageRoomResponse

fun MessageRoomResponse.toData(): MessageRoom = MessageRoom(
    roomId = roomId,
    senderName = senderName,
    message = Message.create(
        senderId = senderId,
        message = recentMessage,
        createdAt = recentMessageTime,
    ),
)

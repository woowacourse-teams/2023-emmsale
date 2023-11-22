package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MessageRoomResponse
import com.emmsale.data.apiModel.response.MessageRoomResponse2
import com.emmsale.data.model.Message
import com.emmsale.data.model.MessageRoom
import com.emmsale.data.model.MessageRoom2

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

fun List<MessageRoomResponse2>.toData(): List<MessageRoom2> = map { it.toData() }

fun MessageRoomResponse2.toData(): MessageRoom2 = MessageRoom2(
    roomId = roomId,
    interlocutor = interlocutor.toData(),
    recentMessage = recentMessage.toData(),
)

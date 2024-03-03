package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.MessageRoomResponse
import com.emmsale.model.MessageRoom

fun List<MessageRoomResponse>.toData(): List<MessageRoom> = map { it.toData() }

fun MessageRoomResponse.toData(): MessageRoom = MessageRoom(
    roomId = roomId,
    interlocutor = interlocutor.toData(),
    recentMessage = recentMessage.toData(),
)

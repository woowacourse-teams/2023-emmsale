package com.emmsale.data.message.mapper

import com.emmsale.data.message.Message
import com.emmsale.data.messageRoom.dto.MessageResponse

fun List<MessageResponse>.toData(): List<Message> = map { it.toData() }

fun MessageResponse.toData(): Message = Message.create(
    senderId = senderId,
    message = message,
    createdAt = createdAt,
)

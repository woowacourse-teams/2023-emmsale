package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MessageResponse
import com.emmsale.data.model.Message

fun List<MessageResponse>.toData(): List<Message> = map { it.toData() }

fun MessageResponse.toData(): Message = Message.create(
    senderId = senderId,
    message = message,
    createdAt = createdAt,
)

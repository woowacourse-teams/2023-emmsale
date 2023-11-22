package com.emmsale.data.mapper

import com.emmsale.data.apiModel.response.MessageResponse
import com.emmsale.data.apiModel.response.MessageResponse2
import com.emmsale.data.model.Message
import com.emmsale.data.model.Message2

fun List<MessageResponse>.toData(): List<Message> = map { it.toData() }

fun MessageResponse.toData(): Message = Message.create(
    senderId = senderId,
    message = message,
    createdAt = createdAt,
)

fun List<MessageResponse2>.toData(): List<Message2> = map { it.toData() }

fun MessageResponse2.toData(): Message2 = Message2.create(
    id = id,
    sender = sender.toData(),
    content = content,
    createdAt = createdAt,
)

package com.emmsale.data.mapper

import com.emmsale.data.network.apiModel.response.MessageResponse
import com.emmsale.model.Message

fun List<MessageResponse>.toData(): List<Message> = map { it.toData() }

fun MessageResponse.toData(): Message = Message(
    id = id,
    sender = sender.toData(),
    content = content,
    createdAt = createdAt,
)

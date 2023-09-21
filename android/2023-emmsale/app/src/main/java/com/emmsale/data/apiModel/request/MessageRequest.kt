package com.emmsale.data.apiModel.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageRequest(
    @SerialName("senderId")
    val senderId: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("content")
    val message: String,
)

package com.emmsale.data.recruitment.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanionRequestBody(
    @SerialName("senderId")
    val senderId: Long,
    @SerialName("receiverId")
    val receiverId: Long,
    @SerialName("message")
    val message: String,
    @SerialName("eventId")
    val eventId: Long,
)
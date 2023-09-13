package com.emmsale.data.apiModel.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MyPostApiModel(
    @SerialName("postId")
    val postId: Long,
    @SerialName("memberId")
    val memberId: Long,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("eventName")
    val eventName: String,
    @SerialName("content")
    val content: String,
    @SerialName("updatedAt")
    val updatedAt: String,
)

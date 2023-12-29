package com.emmsale.data.apiModel.response

import com.emmsale.data.apiModel.serializer.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class MyPostResponse(
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
    @Serializable(with = DateSerializer::class)
    val updatedAt: LocalDate,
)

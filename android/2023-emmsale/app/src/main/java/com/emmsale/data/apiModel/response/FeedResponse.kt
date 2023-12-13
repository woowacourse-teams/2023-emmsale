package com.emmsale.data.apiModel.response

import com.emmsale.data.apiModel.serializer.DateTimeSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class FeedResponse(
    @SerialName("id")
    val id: Long,
    @SerialName("eventId")
    val eventId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("content")
    val content: String,
    @SerialName("writer")
    val writer: MemberResponse,
    @SerialName("images")
    val imageUrls: List<String>,
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("createdAt")
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @SerialName("updatedAt")
    @Serializable(with = DateTimeSerializer::class)
    val updatedAt: LocalDateTime,
)
